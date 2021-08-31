# Changelog

## Version 1.5.0 - 2021-08-22

### 🌟 New library instrumentation

- Library instrumentation for Apache HTTP Client 4.3
  ([#3623](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3623))
- Library instrumentation for Ratpack server
  ([#3749](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3749))

### 📈 Enhancements

- Support Couchbase 3.2.0
  ([#3645](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3645))
- Handle port and IPv6 in forwarded headers
  ([#3651](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3651))
- Set real Hibernate span name on method entry to help samplers
  ([#3603](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3603))
- Reduce overhead of unsampled requests
  ([#3681](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3681))
- Sanitize SQL in Apache Camel instrumentation
  ([#3683](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3683),
   [#3717](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3717))
- Add option to create span on new netty connection
  ([#3707](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3707))
- Propagate context into jdk http client callback
  ([#3719](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3719))
- Instrument Tomcat executor to support async servlets in new Tomcat 9.0.52 release
  ([#3789](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3789))
- Add otlp-logging exporter
  ([#3807](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3807))
- Add new option to support capturing nested client spans of different types
  ([#3691](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3691))
- Propagate context to lettuce callbacks
  ([#3839](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3839))
- Instrument ForkJoinTask.fork()
  ([#3849](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3849))
- Implement a Call.Factory for okhttp 3.x+ library instrumentation
  ([#3812](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3812))
- Record exception in Dubbo instrumentation
  ([#3851](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3851))
- Propagate context to elasticsearch callbacks
  ([#3858](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3858),
   [#3861](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3861))
- Added Vertx http client 4 instrumentation
  ([#3665](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3665))
- Make empty agent bridged context equal root context
  ([#3869](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3869))

### 🛠️ Bug fixes

- Fix OkHttp 3 correlation when using callback under concurrency
  ([#3669](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3669),
   [#3676](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3676))
- Fix Netty span not captured on read timeout
  ([#3613](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3613))
- Fix Netty connection failure handling when listener is lambda
  ([#3569](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3569))
- Fix NullPointerException in Apache HttpAsyncClient instrumentation
  ([#3692](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3692))
- Fix NullPointerException in Tomcat instrumentation
  ([#3705](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3705))
- Fix Apache HttpClient telemetry when host and absolute URI are used
  ([#3694](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3694))
- Fix JDK http client should propagate even when sampled out
  ([#3736](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3736))
- Limit netty exception capture to netty spans
  ([#3809](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3809))
- Fix jetty httpclient returning empty response when instrumented
  ([#3831](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3831),
   [#3833](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3833))
- Don't clobber user decorators in Armeria client instrumentation
  ([#3873](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3873))
- Use valid Java identifiers for message keys
  ([#3863](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3863))
- Fix ClassNotFoundException: HandlerMappingResourceNameFilter in some ear deployments
  ([#3718](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3718))

### 🧰 Tooling

- Improve extension sample documentation and add it to the README file
  ([#3656](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3656))
- Extract muzzle check plugin
  ([#3657](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3657))
- Move instrumentation specific classes out of javaagent-instrumentation-api
  ([#3604](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3604))
- Publish muzzle plugins to Gradle Plugin Portal
  ([#3720](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3720),
   [#3763](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3763))
- Fill `http.client_ip` in ServerInstrumenter
  ([#3756](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3756))
- Fix printMuzzleReferences gradle task
  ([#3808](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3808))
- Introduce stable property for external extensions
  ([#3823](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3823))
- Run tests on j9 JVM during CI
  ([#3764](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3764))
- Support looking up a ContextStore from outside of Advice
  ([#3827](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3827))
- Deprecate MetricExporterFactory
  ([#3862](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3862))
- Don't pass configuration to SDK autoconfigure through system props
  ([#3866](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3866))
- Rename Config get\*Property() methods to get\*()
  ([#3881](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3881))

## Version 1.4.0 - 2021-07-20

### ☢️ Behavioral changes

- Updated all instrumentation names to `io.opentelemetry.{libName}-{libVersion}`
  ([#3411](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3411))
- Updated RabbitMQ to follow semantic conventions
  ([#3425](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3425))

### 🌟 New javaagent instrumentation

- Jetty 9 HTTP client instrumentation
  ([#3079](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3079))

### 🌟 New library instrumentation

- Jetty 9 HTTP client instrumentation
  ([#3079](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3079))
- Jdbc instrumentation
  ([#3367](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3367))

### 📈 Enhancements

- Make @RabbitListener propagate context properly
  ([#3339](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3339))
- Add peer.service to grpc javaagent instrumentation
  ([#3357](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3357))
- Propagate context to cassandra4 callbacks
  ([#3371](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3371))
- Update Armeria instrumentation to support new Armeria 1.9.0 release
  ([#3407](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3407))
- Context propagation for ratpack Execution.fork()
  ([#3416](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3416))

### 🛠️ Bug fixes

- Fix Kafka stream instrumentation to support Kafka 2.6 and above
  ([#3438](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3438))
- Fix Dubbo trace/span cross-process propagation
  ([#3442](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3442))
- Fix `peer.service` configuration mapping
  ([#3378](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3378))

### 🧰 Tooling

- Hide Config#create() method and use builder everywhere
  ([#3338](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3338))
- Ignore task classes using IgnoredTypesConfigurer
  ([#3380](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3380))
- Exclude duplicate classes from final jars
  ([#3432](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3432),
   [#3430](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3430))
- Port AsyncSpanEndStrategy to Instrumenter API
  ([#3262](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3262))
- Rename `opentelemetry-javaagent-api` artifact to `opentelemetry-javaagent-instrumentation-api`
  ([#3513](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3513))

## Version 1.3.1 - 2021-06-23

### 🛠️ Bug fixes

- Fix incorrect dependency in published BOM
  ([#3376](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3376))
- Fix UnsupportedOperationException with reactor-rabbitmq
  ([#3381](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3381))
- Fix Spring JMS not being instrumented
  ([#3359](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3359))

## Version 1.3.0 - 2021-06-17

### ☢️ Behavioral changes

- Update agent logger prefix
  ([#3007](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3007))
- Remove khttp instrumentation
  ([#3087](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3087))
- Enable akka actor instrumentation by default
  ([#3173](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3173))

### 🌟 New javaagent instrumentation

- Spring Integration javaagent instrumentation
  ([#3295](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3295))

### 🌟 New library instrumentation

- Spring Integration library instrumentation
  ([#3120](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3120))

### 📈 Enhancements

- Support peer-service-mapping in OkHttp3 instrumentation
  ([#3063](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3063))
- Low cardinality span names for Hibernate spans
  ([#3106](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3106))
- Propagate context to armeria callbacks
  ([#3108](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3108))
- Add attributes to netty connection failure span
  ([#3115](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3115))
- Defer initialization of OpenTelemetry in spring-boot-autoconfigure
  ([#3171](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3171))
- Support couchbase 3.1.6
  ([#3194](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3194))
- New experimental support for agent extensions
  ([#2881](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2881),
   [#3071](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3071),
   [#3226](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3226),
   [#3237](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3237))
- Propagate context to akka http callbacks
  ([#3263](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3263))

### 🛠️ Bug fixes

- Remove Netty instrumented handler wrapper when original handler is removed
  ([#3026](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3026))
- Fix memory leak when Netty handler is a lambda
  ([#3059](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3059))
- Fix race condition on Undertow
  ([#2992](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2992))
- Remove db.connection_string from redis instrumentation
  ([#3094](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3094))
- Fix context propagation leak in Akka instrumentation
  ([#3099](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3099))
- Fix webflux handler span sporadically not ending
  ([#3150](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3150))
- End span on cancellation of subscription to reactive publishers
  ([#3153](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3153))
- End span on cancellation of Guava future
  ([#3175](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3175))
- Create Netty connection failure span only when first operation fails
  ([#3228](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3228))
- Internal instrumentation should always be enabled by default
  ([#3257](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3257))
- Fix context propagation leak in Akka HTTP instrumentation
  ([#3264](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3264))
- Only include exporters in the `-all` jar
  ([#3286](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3286))
- Fix ForkJoinPool sometimes not instrumented
  ([#3293](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3293))

### 🧰 Tooling

- Migrate MuzzlePlugin to Java
  ([#2996](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2996),
   [#3017](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3017))
- Refactor TypeInstrumentation#transformers() method
  ([#3019](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3019))
- Change a couple of Longs to Integers in Instrumenter API
  ([#3043](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3043))
- Add peer.service to Instrumenter API
  ([#3050](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3050))
- Add response type parameter to db attributes extractor
  ([#3093](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3093))
- Add optimized Attributes implementation for Instrumenter
  ([#3136](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3136))
- Rename ComponentInstaller to AgentListener and add #order() method
  ([#3182](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3182))
- Update ByteBuddy
  ([#3254](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3254))
- Introduce IgnoredTypesConfigurer SPI to enable defining per-module ignores
  ([#3219](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3219))
- Extract agent shadow configuration to conventions script
  ([#3256](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3256))
- Deprecate SpanExporterFactory in favor of ConfigurableSpanExporterProvider
  ([#3299](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3299))
- Refactor span names class
  ([#3281](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3281))
- Move http client/server testing dependencies to internal package
  ([#3305](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/3305))

## Version 1.2.0 - 2021-05-14

### ☢️ Behavioral changes

- AWS lambda flush timeout raised to 10 seconds
  ([#2855](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2855))
- `SERVER` span names improved for Spring MVC, Grails, Wicket, and Struts
  ([#2814](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2814))
- `SERVER` span names improved for Servlet filters
  ([#2887](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2887))
- `SERVER` span names improved for Resteasy
  ([#2900](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2900))
- `SERVER` span names improved for Jersey and CXF
  ([#2919](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2919))
- JAX-RS `@ApplicationPath` annotation captured as part of `SERVER` span name
  ([#2824](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2824))
- RequestDispatcher `forward()` and `include()` internal spans removed
  ([#2816](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2816))
- Raised gRPC min version supported to 1.6 in order to use new gRPC context bridge API
  ([#2948](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2948))

### 🌟 New javaagent instrumentation

- RxJava 3
  ([#2794](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2794))

### 🌟 New library instrumentation

- RxJava 3
  ([#2794](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2794))

### 📈 Enhancements

- Support sub-millisecond precision for start/end times on Java 9+
  ([#2600](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2600))
- `@WithSpan` async support added for methods returning async Reactor 3.x types
  ([#2714](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2714))
- `@WithSpan` async support added for methods returning Guava ListenableFuture
  ([#2811](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2811))
- Semantic attributes `code.namespace` and `code.function` captured on JAX-RS internal spans
  ([#2805](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2805))
- Context propagated to reactor-netty callbacks
  ([#2850](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2850))

### 🛠️ Bug fixes

- gRPC context bridging issues
  ([#2564](https://github.com/open-telemetry/opentelemetry-java-instrumentation/issue/2564),
  [#2959](https://github.com/open-telemetry/opentelemetry-java-instrumentation/issue/2959))
- URL credentials of the form `https://username:password@www.example.com/` no longer captured
  ([#2707](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2707))
- Spring MVC instrumentation can cause Spring MVC to misroute requests under some conditions
  ([#2815](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2815))
- RxJava2 NoSuchFieldError
  ([#2836](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2836))
- Duplicate http client tracing headers
  ([#2842](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2842))
- Netty 4.1 listeners could not be removed by application
  ([#2851](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2851))
- NPE caused in gRPC ProtoReflectionService
  ([#2876](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2876))
- Context leak when using Ratpack
  ([#2910](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2910))
- Context leak when using Jetty
  ([#2920](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2920))
- Servlet instrumentation overwrites setStatus that was set manually earlier
  ([#2929](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2929))
- Spans not captured on interface default methods annotated with JAX-RS annotations
  ([#2930](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2930))

### 🧰 Tooling

- Documented how to write InstrumentationModule line by line
  ([#2793](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2793))
- New instrumenter API used in JMS instrumentation
  ([#2803](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2803))
- Instrumenter API improvements
  ([#2860](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2860))
- Muzzle checks whether used fields are actually declared somewhere
  ([#2870](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2870))
- Extracted javaagent-extension-api from tooling & spi
  ([#2879](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2879))
  - You no longer have to depend on the `javaagent-tooling` module to implement custom instrumentations: a new `javaagent-extension-api` module was introduced, containing all the necessary instrumentation classes and interfaces;
  - `InstrumentationModule` and `TypeInstrumentation` were moved to the `io.opentelemetry.javaagent.extension.instrumentation` package;
  - `AgentElementMatchers`, `ClassLoaderMatcher` and `NameMatchers` were moved to the `io.opentelemetry.javaagent.extension.matcher` package;
  - A new SPI `AgentExtension` was introduced: it replaces `ByteBuddyAgentCustomizer`;
  - `InstrumentationModule#getOrder()` was renamed to `order()`;
  - `InstrumentationModule#additionalHelperClassNames()` has been removed; use `isHelperClass(String)` instead if you use the muzzle compile plugin. If you're not using muzzle, you can override `getMuzzleHelperClassNames()` directly instead;
  - `InstrumentationModule#getAllHelperClassNames()` has been removed; you can call `getMuzzleHelperClassNames()` to retrieve all helper class names instead.

## Version 1.1.0 - 2021-04-14

### ☢️ Behavioral changes

- Update servlet attribute names for log injection, from `traceId` and `spanId` to `trace_id` and
  `span_id`
  ([#2593](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2593))
- Renamed `runtime.jvm.gc.collection` metric to `runtime.jvm.gc.time`
  ([#2616](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2616))

### 🌟 New javaagent instrumentation

- Elasticsearch 7
  ([#2514](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2514),
  [#2528](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2528))
- Couchbase 3.1
  ([#2524](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2524))
- Grails
  ([#2512](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2512))
- RocketMQ
  ([#2263](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2263))
- Lettuce 6
  ([#2589](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2589))
- Servlet 5
  ([#2609](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2609))
- Vaadin web framework
  ([#2619](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2619))
- GWT
  ([#2652](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2652))
- Tapestry web framework
  ([#2690](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2690))
- `@WithSpan` support for methods returning CompletableFuture
  ([#2530](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2530))
- `@WithSpan` support for methods returning async RxJava 2 types
  ([#2530](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2530))

### 🌟 New library instrumentation

- Library instrumentation for AWS SDK v1
  ([#2525](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2525))
- Library instrumentation for Lettuce 5.1
  ([#2533](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2533))
- RocketMQ
  ([#2263](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2263))
- Lettuce 6
  ([#2589](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2589))
- Spring Boot Autoconfigure support for `@WithSpan` methods returning CompletableFuture
  ([#2618](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2618))
- Spring Boot Autoconfigure support for `@WithSpan` methods returning async RxJava 2 types
  ([#2530](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2530))

### 📈 Improvements

- Move attributes to span builder for use by samplers
  ([#2587](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2587))
- Apache Camel - SNS propagation
  ([#2562](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2562))
- Apache Camel - S3 to SQS propagation
  ([#2583](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2583))
- Added `runtime.jvm.gc.count` metric
  ([#2616](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2616))
- Support reactor netty `HttpClient.from` construction
  ([#2650](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2650))
- Improve akka instrumentation
  ([#2737](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2737))
- Record internal metric for SQL cache misses
  ([#2747](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2747))
- End Netty 4.1 client and server spans when the response has completed, instead of when the
  response has started
  ([#2641](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2641))

### 🛠️ Bug fixes

- Fix RestTemplateInterceptor so that it calls endExceptionally() on exception
  ([#2516](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2516))
- Fix app failure under Eclipse OSGi
  ([#2521](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2521))
- Fix undertow span ending too early
  ([#2560](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2560))
- Fix context leak in AWS SDK 2.2 and RocketMQ instrumentations
  ([#2637](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2637))
- Fix hang when a webflux http request is made inside of another webflux http request
  (e.g. auth filter)
  ([#2646](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2646))
- Fix `@WithSpan` instrumentation breaking Java 6 classes
  ([#2699](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2699))
- Fix context not propagated over JMS when explicit destination used
  ([#2702](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2702))
- Fix StackOverflowError if jdbc driver implementation of Connection getMetaData calls Statement
  execute
  ([#2756](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2756))

### 🧰 Tooling

- Make muzzle reference creation package(s) configurable
  ([#2615](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2615))
- Instrumentations now can skip defining context store manually
  ([#2775](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2775))
- New Instrumenter API
  ([#2596](https://github.com/open-telemetry/opentelemetry-java-instrumentation/pull/2596))
