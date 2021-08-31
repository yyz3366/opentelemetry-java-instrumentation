plugins {
  id("otel.java-conventions")
  id("otel.publish-conventions")
}

description = "OpenTelemetry Javaagent testing commons"
group = "io.opentelemetry.javaagent"

sourceSets {
  main {
    val armeriaShadedDeps = project(":testing:armeria-shaded-for-testing")
    output.dir(armeriaShadedDeps.file("build/extracted/shadow"), "builtBy" to ":testing:armeria-shaded-for-testing:extractShadowJar")
  }
}

dependencies {
  api("org.codehaus.groovy:groovy-all")
  api("org.spockframework:spock-core")
  api("org.junit.jupiter:junit-jupiter-api")
  api("org.junit.jupiter:junit-jupiter-params")

  api("io.opentelemetry:opentelemetry-api")
  api("io.opentelemetry:opentelemetry-semconv")
  api("io.opentelemetry:opentelemetry-sdk")
  api("io.opentelemetry:opentelemetry-sdk-testing")
  api("io.opentelemetry:opentelemetry-sdk-metrics")
  api("io.opentelemetry:opentelemetry-sdk-metrics-testing")

  api("org.assertj:assertj-core")

  // Needs to be api dependency due to Spock restriction.
  api("org.awaitility:awaitility")

  compileOnly(project(path = ":testing:armeria-shaded-for-testing", configuration = "shadow"))

  implementation("io.opentelemetry:opentelemetry-proto") {
    // Only need the proto, not gRPC.
    exclude("io.grpc")
  }

  implementation("com.google.guava:guava")
  implementation("net.bytebuddy:byte-buddy")
  implementation("net.bytebuddy:byte-buddy-agent")
  implementation("org.slf4j:slf4j-api")
  implementation("ch.qos.logback:logback-classic")
  implementation("org.slf4j:log4j-over-slf4j")
  implementation("org.slf4j:jcl-over-slf4j")
  implementation("org.slf4j:jul-to-slf4j")
  implementation("io.opentelemetry:opentelemetry-extension-annotations")
  implementation("io.opentelemetry:opentelemetry-exporter-logging")
  implementation(project(":instrumentation-api"))

  annotationProcessor("com.google.auto.service:auto-service")
  compileOnly("com.google.auto.service:auto-service")

  testImplementation(project(":javaagent-instrumentation-api"))
  testImplementation(project(":javaagent-tooling"))
  testImplementation(project(":javaagent-bootstrap"))
  testImplementation(project(":javaagent-extension-api"))
  testImplementation(project(":instrumentation:external-annotations:javaagent"))

  // We have autoservices defined in test subtree, looks like we need this to be able to properly rebuild this
  testAnnotationProcessor("com.google.auto.service:auto-service")
  testCompileOnly("com.google.auto.service:auto-service")
}

tasks {
  javadoc {
    enabled = false
  }
}
