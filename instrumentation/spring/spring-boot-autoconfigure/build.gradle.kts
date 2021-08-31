plugins {
  id("otel.java-conventions")
  id("otel.publish-conventions")
}

group = "io.opentelemetry.instrumentation"

val versions: Map<String, String> by project

dependencies {
  implementation(project(":instrumentation-api-annotation-support"))

  implementation("org.springframework.boot:spring-boot-autoconfigure:${versions["org.springframework.boot"]}")
  annotationProcessor("org.springframework.boot:spring-boot-autoconfigure-processor:${versions["org.springframework.boot"]}")
  implementation("javax.validation:validation-api:2.0.1.Final")

  implementation(project(":instrumentation:spring:spring-web-3.1:library"))
  implementation(project(":instrumentation:spring:spring-webmvc-3.1:library"))
  implementation(project(":instrumentation:spring:spring-webflux-5.0:library"))

  compileOnly("org.springframework.boot:spring-boot-starter-aop:${versions["org.springframework.boot"]}")
  compileOnly("org.springframework.boot:spring-boot-starter-web:${versions["org.springframework.boot"]}")
  compileOnly("org.springframework.boot:spring-boot-starter-webflux:${versions["org.springframework.boot"]}")

  compileOnly("io.opentelemetry:opentelemetry-extension-annotations")
  compileOnly("io.opentelemetry:opentelemetry-extension-trace-propagators")
  compileOnly("io.opentelemetry:opentelemetry-extension-aws")
  compileOnly("io.opentelemetry:opentelemetry-exporter-logging")
  compileOnly("io.opentelemetry:opentelemetry-exporter-jaeger")
  compileOnly("io.opentelemetry:opentelemetry-exporter-otlp")
  compileOnly("io.opentelemetry:opentelemetry-exporter-zipkin")
  compileOnly("io.grpc:grpc-api:1.30.2")

  testImplementation("org.springframework.boot:spring-boot-starter-aop:${versions["org.springframework.boot"]}")
  testImplementation("org.springframework.boot:spring-boot-starter-webflux:${versions["org.springframework.boot"]}")
  testImplementation("org.springframework.boot:spring-boot-starter-web:${versions["org.springframework.boot"]}")
  testImplementation("org.springframework.boot:spring-boot-starter-test:${versions["org.springframework.boot"]}") {
    exclude("org.junit.vintage", "junit-vintage-engine")
  }

  testImplementation("org.assertj:assertj-core")
  testImplementation(project(":testing-common"))
  testImplementation("io.opentelemetry:opentelemetry-sdk")
  testImplementation("io.opentelemetry:opentelemetry-sdk-testing")
  testImplementation("io.opentelemetry:opentelemetry-extension-annotations")
  testImplementation("io.opentelemetry:opentelemetry-extension-trace-propagators")
  testImplementation("io.opentelemetry:opentelemetry-extension-aws")
  testImplementation("io.opentelemetry:opentelemetry-exporter-logging")
  testImplementation("io.opentelemetry:opentelemetry-exporter-jaeger")
  testImplementation("io.opentelemetry:opentelemetry-exporter-otlp")
  testImplementation("io.opentelemetry:opentelemetry-exporter-zipkin")
  testImplementation("io.grpc:grpc-api:1.30.2")
  testImplementation("io.grpc:grpc-netty-shaded:1.30.2")
  testImplementation(project(":instrumentation-api-annotation-support"))

  // this only exists to make Intellij happy since it doesn't (currently at least) understand our
  // inclusion of this artifact inside of :instrumentation-api
  compileOnly(project(":instrumentation-api-caching"))
}

tasks.compileTestJava {
  options.compilerArgs.add("-parameters")
}
