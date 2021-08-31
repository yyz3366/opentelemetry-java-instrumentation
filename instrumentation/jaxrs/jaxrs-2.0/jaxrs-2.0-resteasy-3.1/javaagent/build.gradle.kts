plugins {
  id("otel.javaagent-instrumentation")
}

muzzle {
  // Cant assert fails because muzzle assumes all instrumentations will fail
  // Instrumentations in jaxrs-2.0-common will pass

  // Resteasy changes a class's package in 3.1.0 then moves it back in 3.5.0 and then moves it forward again in 4.0.0
  // so the jaxrs-2.0-resteasy-3.0 module applies to [3.0, 3.1) and [3.5, 4.0)
  // and the jaxrs-2.0-resteasy-3.1 module applies to [3.1, 3.5) and [4.0, )
  pass {
    group.set("org.jboss.resteasy")
    module.set("resteasy-jaxrs")
    versions.set("[3.1.0.Final,3.5.0.Final)")
  }

  pass {
    group.set("org.jboss.resteasy")
    module.set("resteasy-core")
    versions.set("[4.0.0.Final,)")
  }
}

dependencies {
  compileOnly("javax.ws.rs:javax.ws.rs-api:2.0")
  library("org.jboss.resteasy:resteasy-jaxrs:3.1.0.Final")

  implementation(project(":instrumentation:jaxrs:jaxrs-2.0:jaxrs-2.0-common:javaagent"))
  implementation(project(":instrumentation:jaxrs:jaxrs-2.0:jaxrs-2.0-resteasy-common:javaagent"))

  testInstrumentation(project(":instrumentation:servlet:servlet-3.0:javaagent"))
  testInstrumentation(project(":instrumentation:servlet:servlet-javax-common:javaagent"))

  testImplementation(project(":instrumentation:jaxrs:jaxrs-2.0:jaxrs-2.0-testing"))
  testImplementation("org.eclipse.jetty:jetty-webapp:9.4.6.v20170531")

  testLibrary("org.jboss.resteasy:resteasy-undertow:3.1.0.Final") {
    exclude("org.jboss.resteasy", "resteasy-client")
  }
  testLibrary("org.jboss.resteasy:resteasy-servlet-initializer:3.1.0.Final")

  // artifact name changed from 'resteasy-jaxrs' to 'resteasy-core' starting from version 4.0.0
  latestDepTestLibrary("org.jboss.resteasy:resteasy-core:+")
}

tasks {
  named<Test>("test") {
    systemProperty("testLatestDeps", findProperty("testLatestDeps") as Boolean)
  }

  withType<Test>().configureEach {
    // TODO run tests both with and without experimental span attributes
    jvmArgs("-Dotel.instrumentation.jaxrs.experimental-span-attributes=true")
  }
}

if (findProperty("testLatestDeps") as Boolean) {
  configurations {
    // artifact name changed from 'resteasy-jaxrs' to 'resteasy-core' starting from version 4.0.0
    testImplementation {
      exclude("org.jboss.resteasy", "resteasy-jaxrs")
    }
  }
}
