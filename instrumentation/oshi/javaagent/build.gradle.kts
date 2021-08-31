plugins {
  id("otel.javaagent-instrumentation")
}

muzzle {
  pass {
    group.set("com.github.oshi")
    module.set("oshi-core")
    versions.set("[5.3.1,)")
  }
}

dependencies {
  implementation(project(":instrumentation:oshi:library"))

  library("com.github.oshi:oshi-core:5.3.1")

  testImplementation("com.google.guava:guava")
}
