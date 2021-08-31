plugins {
  id("otel.library-instrumentation")
}

dependencies {
  compileOnly("io.opentelemetry:opentelemetry-sdk")
  compileOnly("io.opentelemetry:opentelemetry-sdk-extension-autoconfigure")

  library("com.amazonaws:aws-lambda-java-core:1.0.0")
  // First version to includes support for SQSEvent, currently the most popular message queue used
  // with lambda.
  // NB: 2.2.0 includes a class called SQSEvent but isn't usable due to it returning private classes
  // in public API.
  library("com.amazonaws:aws-lambda-java-events:2.2.1")

  compileOnly("com.fasterxml.jackson.core:jackson-databind")
  compileOnly("commons-io:commons-io:2.2")
  compileOnly("org.slf4j:slf4j-api")

  implementation("io.opentelemetry:opentelemetry-extension-aws")

  // 1.2.0 allows to get the function ARN
  testLibrary("com.amazonaws:aws-lambda-java-core:1.2.0")

  testImplementation("com.fasterxml.jackson.core:jackson-databind")
  testImplementation("commons-io:commons-io:2.2")

  testImplementation("io.opentelemetry:opentelemetry-sdk-extension-autoconfigure")
  testImplementation("io.opentelemetry:opentelemetry-extension-trace-propagators")
  testImplementation("com.google.guava:guava")

  testImplementation(project(":instrumentation:aws-lambda-1.0:testing"))
  testImplementation("org.mockito:mockito-core")
  testImplementation("org.assertj:assertj-core")
}
