plugins {
  id("otel.javaagent-instrumentation")
  id("org.unbroken-dome.test-sets")
}

muzzle {
  pass {
    group.set("org.apache.logging.log4j")
    module.set("log4j-core")
    versions.set("[2.13.2,)")
    assertInverse.set(true)
  }
}

testSets {
  // Very different codepaths when threadlocals are enabled or not so we check both.
  // Regression test for https://github.com/open-telemetry/opentelemetry-java-instrumentation/issues/2403
  create("testDisableThreadLocals") {
    dirName = "test"
  }
}

dependencies {
  library("org.apache.logging.log4j:log4j-core:2.13.2")

  implementation(project(":instrumentation:log4j:log4j-2.13.2:library"))

  testImplementation(project(":instrumentation:log4j:log4j-2-testing"))
}

tasks {
  val testDisableThreadLocals by existing(Test::class) {
    jvmArgs("-Dlog4j2.is.webapp=false")
    jvmArgs("-Dlog4j2.enable.threadlocals=false")
  }

  // Threadlocals are always false if is.webapp is true, so we make sure to override it because as of
  // now testing-common includes jetty / servlet.
  named<Test>("test") {
    jvmArgs("-Dlog4j2.is.webapp=false")
    jvmArgs("-Dlog4j2.enable.threadlocals=true")
  }

  named("check") {
    dependsOn(testDisableThreadLocals)
  }
}
