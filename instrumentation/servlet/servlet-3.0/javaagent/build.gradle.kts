plugins {
  id("otel.javaagent-instrumentation")
}
muzzle {
  pass {
    group.set("javax.servlet")
    module.set("javax.servlet-api")
    versions.set("[3.0,)")
    assertInverse.set(true)
  }
  fail {
    group.set("javax.servlet")
    module.set("servlet-api")
    versions.set("(,)")
  }
}

dependencies {
  compileOnly("javax.servlet:javax.servlet-api:3.0.1")
  api(project(":instrumentation:servlet:servlet-3.0:library"))
  implementation(project(":instrumentation:servlet:servlet-common:javaagent"))

  testInstrumentation(project(":instrumentation:jetty:jetty-8.0:javaagent"))
  testInstrumentation(project(":instrumentation:servlet:servlet-javax-common:javaagent"))

  testLibrary("org.eclipse.jetty:jetty-server:8.0.0.v20110901")
  testLibrary("org.eclipse.jetty:jetty-servlet:8.0.0.v20110901")
  testLibrary("org.apache.tomcat.embed:tomcat-embed-core:8.0.41")
  testLibrary("org.apache.tomcat.embed:tomcat-embed-jasper:8.0.41")

  // Jetty 10 seems to refuse to run on java8.
  // TODO: we need to setup separate test for Jetty 10 when that is released.
  latestDepTestLibrary("org.eclipse.jetty:jetty-server:9.+")
  latestDepTestLibrary("org.eclipse.jetty:jetty-servlet:9.+")

  latestDepTestLibrary("org.apache.tomcat.embed:tomcat-embed-core:9.+")
  latestDepTestLibrary("org.apache.tomcat.embed:tomcat-embed-jasper:9.+")
}
