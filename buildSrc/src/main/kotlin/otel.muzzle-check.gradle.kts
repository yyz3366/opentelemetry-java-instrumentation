/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

import io.opentelemetry.instrumentation.gradle.muzzle.AcceptableVersions
import io.opentelemetry.instrumentation.gradle.muzzle.BogusClassLoader
import io.opentelemetry.instrumentation.gradle.muzzle.MuzzleDirective
import io.opentelemetry.instrumentation.gradle.muzzle.MuzzleExtension
import org.apache.maven.repository.internal.MavenRepositorySystemUtils
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.RepositorySystemSession
import org.eclipse.aether.artifact.Artifact
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory
import org.eclipse.aether.repository.LocalRepository
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.resolution.VersionRangeRequest
import org.eclipse.aether.resolution.VersionRangeResult
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory
import org.eclipse.aether.spi.connector.transport.TransporterFactory
import org.eclipse.aether.transport.http.HttpTransporterFactory
import org.eclipse.aether.version.Version
import java.net.URL
import java.net.URLClassLoader
import java.util.stream.StreamSupport

plugins {
  `java-library`
}

// Select a random set of versions to test
val RANGE_COUNT_LIMIT = 10

val muzzleConfig = extensions.create<MuzzleExtension>("muzzle")

val muzzleTooling by configurations.creating {
  isCanBeConsumed = false
  isCanBeResolved = true
}
val muzzleBootstrap by configurations.creating {
  isCanBeConsumed = false
  isCanBeResolved = true
}

val compileMuzzle by tasks.registering {
  dependsOn(muzzleBootstrap)
  dependsOn(muzzleTooling)
  dependsOn(tasks.named("classes"))
}

val muzzle by tasks.registering {
  group = "Muzzle"
  description = "Run instrumentation muzzle on compile time dependencies"
  dependsOn(compileMuzzle)
}

tasks.register("printMuzzleReferences") {
  group = "Muzzle"
  description = "Print references created by instrumentation muzzle"
  dependsOn(compileMuzzle)
  doLast {
    val instrumentationCL = createInstrumentationClassloader()
    val assertionMethod = instrumentationCL
      .loadClass(
        "io.opentelemetry.javaagent.tooling.muzzle.matcher.MuzzleGradlePluginUtil")
      .getMethod("printMuzzleReferences", ClassLoader::class.java)
    assertionMethod.invoke(null, instrumentationCL)
  }
}

val projectRepositories = mutableListOf<RemoteRepository>().apply {
  // Manually add mavenCentral until https://github.com/gradle/gradle/issues/17295
  // Adding mavenLocal is much more complicated but hopefully isn't required for normal usage of
  // Muzzle.
  add(
    RemoteRepository.Builder(
      "MavenCentral", "default", "https://repo.maven.apache.org/maven2/")
      .build())
  for (repository in repositories) {
    if (repository is MavenArtifactRepository) {
      add(
        RemoteRepository.Builder(
          repository.getName(),
          "default",
          repository.url.toString())
          .build())
    }
  }
}.toList()

val hasRelevantTask = gradle.startParameter.taskNames.any {
  // removing leading ':' if present
  val taskName = it.removePrefix(":")
  val projectPath = project.path.substring(1)
  // Either the specific muzzle task in this project or the top level, full-project
  // muzzle task.
  // Either the specific muzzle task in this project or the top level, full-project
  // muzzle task.
  taskName == "${projectPath}:muzzle" || taskName == "muzzle"
}

if (hasRelevantTask) {
  val system = newRepositorySystem()
  val session = newRepositorySystemSession(system)

  afterEvaluate {
    var runAfter = muzzle

    for (muzzleDirective in muzzleConfig.directives.get()) {
      logger.info("configured ${muzzleDirective}")

      if (muzzleDirective.coreJdk.get()) {
        runAfter = addMuzzleTask(muzzleDirective, null, runAfter)
      } else {
        for (singleVersion in muzzleDirectiveToArtifacts(muzzleDirective, system, session)) {
          runAfter = addMuzzleTask(muzzleDirective, singleVersion, runAfter)
        }
        if (muzzleDirective.assertInverse.get()) {
          for (inverseDirective in inverseOf(muzzleDirective, system, session)) {
            for (singleVersion in muzzleDirectiveToArtifacts(inverseDirective, system, session)) {
              runAfter = addMuzzleTask(inverseDirective, singleVersion, runAfter)
            }
          }
        }
      }
    }
  }
}

fun createInstrumentationClassloader(): ClassLoader {
  logger.info("Creating instrumentation classpath for: ${name}")
  val runtimeClasspath = sourceSets.main.get().runtimeClasspath
  return classpathLoader(runtimeClasspath, createMuzzleCheckLoader())
}

fun classpathLoader(classpath: FileCollection, parent: ClassLoader): ClassLoader {
  val urls: Array<URL> = StreamSupport.stream(classpath.spliterator(), false)
    .map {
      logger.info("--${it}")
      it.toURI().toURL()
    }
    .toArray(::arrayOfNulls)
  return URLClassLoader(urls, parent)
}

fun createMuzzleCheckLoader(): ClassLoader {
  logger.info("creating classpath for auto-tooling")
  return classpathLoader(muzzleTooling, ClassLoader.getPlatformClassLoader())
}

fun newRepositorySystem(): RepositorySystem {
  return MavenRepositorySystemUtils.newServiceLocator().apply {
    addService(RepositoryConnectorFactory::class.java, BasicRepositoryConnectorFactory::class.java)
    addService(TransporterFactory::class.java, HttpTransporterFactory::class.java)
  }.run {
    getService(RepositorySystem::class.java)
  }
}

fun newRepositorySystemSession(system: RepositorySystem): RepositorySystemSession {
  val muzzleRepo = file("${buildDir}/muzzleRepo")
  val localRepo = LocalRepository(muzzleRepo)
  return MavenRepositorySystemUtils.newSession().apply {
    setLocalRepositoryManager(system.newLocalRepositoryManager(this, localRepo))
  }
}

fun addMuzzleTask(muzzleDirective: MuzzleDirective, versionArtifact: Artifact?, runAfter: TaskProvider<Task>)
  : TaskProvider<Task> {
  val taskName = if (versionArtifact == null) {
    "muzzle-Assert${muzzleDirective}"
  } else {
    StringBuilder("muzzle-Assert").apply {
      if (muzzleDirective.assertPass.isPresent) {
        append("Pass")
      } else {
        append("Fail")
      }
      append('-')
        .append(versionArtifact.groupId)
        .append('-')
        .append(versionArtifact.artifactId)
        .append('-')
        .append(versionArtifact.version)
      if (!muzzleDirective.name.get().isEmpty()) {
        append(muzzleDirective.nameSlug)
      }
    }.run { toString() }
  }
  val config = configurations.create(taskName)
  if (versionArtifact != null) {
    val dep = (dependencies.create(versionArtifact.run { "${groupId}:${artifactId}:${version}" }) as ModuleDependency).apply {
      isTransitive = true
      exclude("com.sun.jdmk", "jmxtools")
      exclude("com.sun.jmx", "jmxri")
      for (excluded in muzzleDirective.excludedDependencies.get()) {
        val (group, module) = excluded.split(':')
        exclude(group, module)
      }
    }
    config.dependencies.add(dep)

    for (additionalDependency in muzzleDirective.additionalDependencies.get()) {
      val additional = if (additionalDependency.count { it == ':' } < 2) {
        // Dependency definition without version, use the artifact's version.
        "${additionalDependency}:${versionArtifact.version}"
      } else {
        additionalDependency
      }
      val additionalDep = (dependencies.create(additional) as ModuleDependency).apply {
        isTransitive = true
      }
      config.dependencies.add(additionalDep)
    }
  }

  val muzzleTask = tasks.register(taskName) {
    dependsOn(configurations.named("runtimeClasspath"))
    doLast {
      val instrumentationCL = createInstrumentationClassloader()
      val ccl = Thread.currentThread().contextClassLoader
      val bogusLoader = BogusClassLoader()
      val userCL = createClassLoaderForTask(config)
      Thread.currentThread().contextClassLoader = bogusLoader
      try {
        // find all instrumenters, get muzzle, and assert
        val assertionMethod = instrumentationCL
          .loadClass("io.opentelemetry.javaagent.tooling.muzzle.matcher.MuzzleGradlePluginUtil")
          .getMethod(
            "assertInstrumentationMuzzled",
            ClassLoader::class.java,
            ClassLoader::class.java,
            Boolean::class.javaPrimitiveType)
        assertionMethod.invoke(
          null,
          instrumentationCL,
          userCL,
          muzzleDirective.assertPass.get())
      } finally {
        Thread.currentThread().contextClassLoader = ccl
      }

      for (thread in Thread.getAllStackTraces().keys) {
        if (thread.contextClassLoader === bogusLoader
          || thread.contextClassLoader === instrumentationCL
          || thread.contextClassLoader === userCL) {
          throw GradleException(
            "Task ${taskName} has spawned a thread: ${thread} with classloader ${thread.contextClassLoader}. " +
              "This will prevent GC of dynamic muzzle classes. Aborting muzzle run.")
        }
      }
    }
  }

  runAfter.configure { finalizedBy(muzzleTask) }
  return muzzleTask
}

fun createClassLoaderForTask(muzzleTaskConfiguration: Configuration): ClassLoader {
  val userUrls = objects.fileCollection()
  logger.info("Creating task classpath")
  userUrls.from(muzzleTaskConfiguration.resolvedConfiguration.files)
  return classpathLoader(userUrls.plus(muzzleBootstrap), ClassLoader.getPlatformClassLoader())
}

fun inverseOf(muzzleDirective: MuzzleDirective, system: RepositorySystem, session: RepositorySystemSession): Set<MuzzleDirective> {
  val inverseDirectives = mutableSetOf<MuzzleDirective>()

  val allVersionsArtifact = DefaultArtifact(
    muzzleDirective.group.get(), muzzleDirective.module.get(), "jar", "[,)")
  val directiveArtifact = DefaultArtifact(
    muzzleDirective.group.get(),
    muzzleDirective.module.get(),
    "jar",
    muzzleDirective.versions.get())

  val repos = projectRepositories
  val allRangeRequest = VersionRangeRequest().apply {
    repositories = repos
    artifact = allVersionsArtifact
  }
  val allRangeResult = system.resolveVersionRange(session, allRangeRequest)

  val rangeRequest = VersionRangeRequest().apply {
    repositories = repos
    artifact = directiveArtifact
  }
  val rangeResult = system.resolveVersionRange(session, rangeRequest)

  allRangeResult.versions.removeAll(rangeResult.versions)

  for (version in filterVersions(allRangeResult, muzzleDirective.normalizedSkipVersions)) {
    val inverseDirective = objects.newInstance(MuzzleDirective::class).apply {
      group.set(muzzleDirective.group)
      module.set(muzzleDirective.module)
      versions.set(version)
      assertPass.set(!muzzleDirective.assertPass.get())
      excludedDependencies.set(muzzleDirective.excludedDependencies)
    }
    inverseDirectives.add(inverseDirective)
  }

  return inverseDirectives
}

fun filterVersions(range: VersionRangeResult, skipVersions: Set<String>) = sequence {
  val predicate = AcceptableVersions(skipVersions)
  if (predicate.test(range.lowestVersion)) {
    yield(range.lowestVersion.toString())
  }
  if (predicate.test(range.highestVersion)) {
    yield(range.highestVersion.toString())
  }

  val copy: List<Version> = range.versions.shuffled()
  for (version in copy) {
    if (predicate.test(version)) {
      yield(version.toString())
    }
  }
}.distinct().take(RANGE_COUNT_LIMIT)

fun muzzleDirectiveToArtifacts(muzzleDirective: MuzzleDirective, system: RepositorySystem, session: RepositorySystemSession) = sequence<Artifact> {
  val directiveArtifact: Artifact = DefaultArtifact(
    muzzleDirective.group.get(),
    muzzleDirective.module.get(),
    "jar",
    muzzleDirective.versions.get())

  val rangeRequest = VersionRangeRequest().apply {
    repositories = projectRepositories
    artifact = directiveArtifact
  }
  val rangeResult = system.resolveVersionRange(session, rangeRequest)

  val allVersionArtifacts = filterVersions(rangeResult, muzzleDirective.normalizedSkipVersions)
    .map {
      DefaultArtifact(
        muzzleDirective.group.get(),
        muzzleDirective.module.get(),
        "jar",
        it)
    }

  allVersionArtifacts.ifEmpty {
    throw GradleException("No muzzle artifacts found for $muzzleDirective")
  }

  yieldAll(allVersionArtifacts)
}
