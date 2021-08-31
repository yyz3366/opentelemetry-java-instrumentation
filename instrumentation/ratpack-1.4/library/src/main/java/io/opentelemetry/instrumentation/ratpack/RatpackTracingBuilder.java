/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.ratpack;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.InstrumenterBuilder;
import io.opentelemetry.instrumentation.api.instrumenter.http.HttpSpanNameExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.http.HttpSpanStatusExtractor;
import io.opentelemetry.instrumentation.ratpack.internal.RatpackNetAttributesExtractor;
import java.util.ArrayList;
import java.util.List;
import ratpack.http.Request;
import ratpack.http.Response;

/** A builder for {@link RatpackTracing}. */
public final class RatpackTracingBuilder {

  private static final String INSTRUMENTATION_NAME = "io.opentelemetry.ratpack-1.4";

  private final OpenTelemetry openTelemetry;

  private final List<AttributesExtractor<? super Request, ? super Response>> additionalExtractors =
      new ArrayList<>();

  RatpackTracingBuilder(OpenTelemetry openTelemetry) {
    this.openTelemetry = openTelemetry;
  }

  /**
   * Adds an additional {@link AttributesExtractor} to invoke to set attributes to instrumented
   * items. The {@link AttributesExtractor} will be executed after all default extractors.
   */
  public RatpackTracingBuilder addAttributeExtractor(
      AttributesExtractor<? super Request, ? super Response> attributesExtractor) {
    additionalExtractors.add(attributesExtractor);
    return this;
  }

  /** Returns a new {@link RatpackTracing} with the configuration of this builder. */
  public RatpackTracing build() {
    RatpackNetAttributesExtractor netAttributes = new RatpackNetAttributesExtractor();
    RatpackHttpAttributesExtractor httpAttributes = new RatpackHttpAttributesExtractor();

    InstrumenterBuilder<Request, Response> builder =
        Instrumenter.newBuilder(
            openTelemetry, INSTRUMENTATION_NAME, HttpSpanNameExtractor.create(httpAttributes));

    builder.setSpanStatusExtractor(HttpSpanStatusExtractor.create(httpAttributes));
    builder.addAttributesExtractor(netAttributes);
    builder.addAttributesExtractor(httpAttributes);
    builder.addAttributesExtractors(additionalExtractors);

    return new RatpackTracing(builder.newServerInstrumenter(new RatpackGetter()));
  }
}
