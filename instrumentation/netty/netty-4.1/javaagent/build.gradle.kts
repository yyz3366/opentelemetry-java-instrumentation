plugins {
  id("otel.javaagent-instrumentation")
}

muzzle {
  pass {
    group.set("io.netty")
    module.set("netty-codec-http")
    versions.set("[4.1.0.Final,5.0.0)")
    assertInverse.set(true)
  }
  pass {
    group.set("io.netty")
    module.set("netty-all")
    versions.set("[4.1.0.Final,5.0.0)")
    excludeDependency("io.netty:netty-tcnative")
    assertInverse.set(true)
  }
  fail {
    group.set("io.netty")
    module.set("netty")
    versions.set("[,]")
  }
}

dependencies {
  library("io.netty:netty-codec-http:4.1.0.Final")
  api(project(":instrumentation:netty:netty-4.1:library"))
  implementation(project(":instrumentation:netty:netty-4-common:javaagent"))

  // Contains logging handler
  testLibrary("io.netty:netty-handler:4.1.0.Final")
  testLibrary("io.netty:netty-transport-native-epoll:4.1.0.Final:linux-x86_64")

  // first version with kqueue, add it only as a compile time dependency
  testCompileOnly("io.netty:netty-transport-native-kqueue:4.1.11.Final:osx-x86_64")

  latestDepTestLibrary(enforcedPlatform("io.netty:netty-bom:(,5.0)"))
  latestDepTestLibrary("io.netty:netty-codec-http:(,5.0)")
  latestDepTestLibrary("io.netty:netty-handler:(,5.0)")
  latestDepTestLibrary("io.netty:netty-transport-native-epoll:(,5.0):linux-x86_64")
  latestDepTestLibrary("io.netty:netty-transport-native-kqueue:(,5.0):osx-x86_64")
}

tasks {
  val testConnectionSpan by registering(Test::class) {
    filter {
      includeTestsMatching("Netty41ConnectionSpanTest")
      isFailOnNoMatchingTests = false
    }
    include("**/Netty41ConnectionSpanTest.*")
    jvmArgs("-Dotel.instrumentation.netty.always-create-connect-span=true")
  }

  named<Test>("test") {
    systemProperty("testLatestDeps", findProperty("testLatestDeps") as Boolean)

    dependsOn(testConnectionSpan)
    filter {
      excludeTestsMatching("Netty41ConnectionSpanTest")
      isFailOnNoMatchingTests = false
    }
  }
}

if (!(findProperty("testLatestDeps") as Boolean)) {
  // No BOM for 4.1.0 so we can't use enforcedPlatform to override our transitive version
  // management, so hook into the resolutionStrategy.
  configurations.configureEach {
    if (!name.contains("muzzle")) {
      resolutionStrategy.eachDependency {
        if (requested.group == "io.netty" && requested.name != "netty-bom" && !requested.name.startsWith("netty-transport-native")) {
          useVersion("4.1.0.Final")
        }
      }
    }
  }
}
