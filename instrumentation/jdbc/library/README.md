# Manual Instrumentation for JDBC

Provides OpenTelemetry instrumentation for
[Java JDBC API](https://docs.oracle.com/javase/8/docs/technotes/guides/jdbc/).

## Quickstart

### Add these dependencies to your project.

Replace `OPENTELEMETRY_VERSION` with the latest stable
[release](https://mvnrepository.com/artifact/io.opentelemetry). `Minimum version: 1.4.0`

For Maven add to your `pom.xml`:

```xml
<dependencies>
  <dependency>
    <groupId>io.opentelemetry.instrumentation</groupId>
    <artifactId>opentelemetry-jdbc</artifactId>
    <version>OPENTELEMETRY_VERSION</version>
  </dependency>
</dependencies>
```

For Gradle add to your dependencies:

```groovy
implementation("io.opentelemetry.instrumentation:opentelemetry-jdbc:OPENTELEMETRY_VERSION")
```

##### Usage

There are three possible ways to activate the OpenTelemetry JDBC instrumentation. The first way is more preferable for
DI frameworks which uses connection pools, as it wraps a `DataSource` with a special OpenTelemetry wrapper. The second
one requires to change the connection URL and switch to use a special OpenTelemetry driver.

### Datasource way

If your application uses a DataSource, simply wrap your current DataSource object with `OpenTelemetryDataSource`.
`OpenTelemetryDataSource` has a constructor method that accepts the `DataSource` to wrap. This is by far the simplest
method especially if you use a dependency injection (DI) frameworks such as
[Spring Framework](https://spring.io/projects/spring-framework), [Micronaut](https://micronaut.io),
[Quarkus](https://quarkus.io), or [Guice](https://github.com/google/guice).

```java
import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Configuration;
import io.opentelemetry.instrumentation.jdbc.datasource.OpenTelemetryDataSource;

@Configuration
public class DataSourceConfig {

  @Bean
  public DataSource dataSource() {
    BasicDataSource dataSource = new BasicDataSource();
    dataSource.setDriverClassName("org.postgresql.Driver");
    dataSource.setUrl("jdbc:postgresql://127.0.0.1:5432/example");
    dataSource.setUsername("postgres");
    dataSource.setPassword("root");
    return new OpenTelemetryDataSource(dataSource);
  }

}
```

### Driver way

1. Activate tracing for JDBC connections by setting `jdbc:otel:` prefix to the JDBC URL:

```
jdbc:otel:h2:mem:test
```

2. Set the driver class to `io.opentelemetry.instrumentation.jdbc.OpenTelemetryDriver` and initialize the driver with:

```java
Class.forName("io.opentelemetry.instrumentation.jdbc.OpenTelemetryDriver");
```
