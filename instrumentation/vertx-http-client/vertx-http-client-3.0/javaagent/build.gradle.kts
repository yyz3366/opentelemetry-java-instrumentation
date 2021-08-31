plugins {
  id("otel.javaagent-instrumentation")
}

muzzle {
  pass {
    group.set("io.vertx")
    module.set("vertx-core")
    versions.set("[3.0.0,4.0.0)")
    assertInverse.set(true)
  }
}

dependencies {
  library("io.vertx:vertx-core:3.0.0")

  implementation(project(":instrumentation:vertx-http-client:vertx-http-client-common:javaagent"))

  // We need both version as different versions of Vert.x use different versions of Netty
  testInstrumentation(project(":instrumentation:netty:netty-4.0:javaagent"))
  testInstrumentation(project(":instrumentation:netty:netty-4.1:javaagent"))

  // Vert.x 4.0 is incompatible with our tests.
  // 3.9.7 Requires Netty 4.1.60, no other version works with it.
  latestDepTestLibrary(enforcedPlatform("io.netty:netty-bom:4.1.60.Final"))
  latestDepTestLibrary("io.vertx:vertx-core:3.+")
}

tasks {
  named<Test>("test") {
    systemProperty("testLatestDeps", findProperty("testLatestDeps") as Boolean)
  }
}
