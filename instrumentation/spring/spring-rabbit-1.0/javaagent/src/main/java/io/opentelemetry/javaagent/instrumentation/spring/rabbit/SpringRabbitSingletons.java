/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.spring.rabbit;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.SpanNameExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.messaging.MessagingSpanNameExtractor;
import org.springframework.amqp.core.Message;

public final class SpringRabbitSingletons {

  private static final String INSTRUMENTATION_NAME = "io.opentelemetry.javaagent.spring-rabbit-1.0";

  private static final Instrumenter<Message, Void> INSTRUMENTER;

  static {
    SpringRabbitMessageAttributesExtractor attributesExtractor =
        new SpringRabbitMessageAttributesExtractor();
    SpanNameExtractor<Message> spanNameExtractor =
        MessagingSpanNameExtractor.create(attributesExtractor);

    INSTRUMENTER =
        Instrumenter.<Message, Void>newBuilder(
                GlobalOpenTelemetry.get(), INSTRUMENTATION_NAME, spanNameExtractor)
            .addAttributesExtractor(attributesExtractor)
            .newConsumerInstrumenter(new MessageHeaderGetter());
  }

  public static Instrumenter<Message, Void> instrumenter() {
    return INSTRUMENTER;
  }

  private SpringRabbitSingletons() {}
}
