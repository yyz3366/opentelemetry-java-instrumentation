import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.time.Duration

plugins {
  id("otel.java-conventions")
}

description = "smoke-tests"

otelJava {
  // we only need to run the Spock test itself under a single Java version, and the Spock test in
  // turn is parameterized and runs the test using different docker containers that run different
  // Java versions
  minJavaVersionSupported.set(JavaVersion.VERSION_11)
  maxJavaVersionForTests.set(JavaVersion.VERSION_11)
}

val dockerJavaVersion = "3.2.5"
dependencies {
  testCompileOnly("com.google.auto.value:auto-value-annotations")
  testAnnotationProcessor("com.google.auto.value:auto-value")

  api("org.spockframework:spock-core")
  api(project(":testing-common"))

  implementation(platform("io.grpc:grpc-bom:1.33.1"))
  implementation("org.slf4j:slf4j-api")
  implementation("io.opentelemetry:opentelemetry-api")
  implementation("io.opentelemetry:opentelemetry-proto")
  implementation("org.testcontainers:testcontainers")
  implementation("com.fasterxml.jackson.core:jackson-databind")
  implementation("com.google.protobuf:protobuf-java-util:3.12.4")
  implementation("io.grpc:grpc-netty-shaded")
  implementation("io.grpc:grpc-protobuf")
  implementation("io.grpc:grpc-stub")

  testImplementation("com.github.docker-java:docker-java-core:$dockerJavaVersion")
  testImplementation("com.github.docker-java:docker-java-transport-httpclient5:$dockerJavaVersion")

  // make IntelliJ see shaded Armeria
  testCompileOnly(project(path = ":testing:armeria-shaded-for-testing", configuration = "shadow"))
}

tasks {
  test {
    inputs.files(project(":javaagent").tasks.getByName("fullJavaagentJar").outputs.files)

    testLogging.showStandardStreams = true

    // TODO investigate why smoke tests occasionally hang forever
    //  this needs to be long enough so that smoke tests that are just running slow don"t time out
    timeout.set(Duration.ofMinutes(45))

    // We enable/disable smoke tests based on the java version requests
    // In addition to that we disable them on normal test task to only run when explicitly requested.
    enabled = enabled && gradle.startParameter.taskNames.any { it.startsWith(":smoke-tests:") }

    val suites = mapOf(
      "glassfish" to listOf("**/GlassFishSmokeTest.*"),
      "jetty" to listOf("**/JettySmokeTest.*"),
      "liberty" to listOf("**/LibertySmokeTest.*", "**/LibertyServletOnlySmokeTest.*"),
      "tomcat" to listOf("**/TomcatSmokeTest.*"),
      "tomee" to listOf("**/TomeeSmokeTest.*"),
      "wildfly" to listOf("**/WildflySmokeTest.*")
    )

    val smokeTestSuite: String? by project
    if (smokeTestSuite != null) {
      val suite = suites[smokeTestSuite]
      if (suite != null) {
        include(suite)
      } else if (smokeTestSuite == "other") {
        suites.values.forEach {
          exclude(it)
        }
      } else {
        throw GradleException("Unknown smoke test suite: $smokeTestSuite")
      }
    }

    val shadowTask = project(":javaagent").tasks.named<ShadowJar>("fullJavaagentJar").get()
    inputs.files(layout.files(shadowTask))

    doFirst {
      jvmArgs("-Dio.opentelemetry.smoketest.agent.shadowJar.path=${shadowTask.archiveFile.get()}")
    }
  }
}
