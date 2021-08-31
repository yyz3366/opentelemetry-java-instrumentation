/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.api.tracer;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.instrumenter.SpanKey;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * This class encapsulates the context key for storing the current {@link SpanKind#CONSUMER} span in
 * the {@link Context}.
 */
public final class ConsumerSpan {

  /**
   * Returns true when a {@link SpanKind#CONSUMER} span is present in the passed {@code context}.
   */
  public static boolean exists(Context context) {
    return fromContextOrNull(context) != null;
  }

  /**
   * Returns span of type {@link SpanKind#CONSUMER} from the given context or {@code null} if not
   * found.
   */
  @Nullable
  public static Span fromContextOrNull(Context context) {
    return SpanKey.CONSUMER.fromContextOrNull(context);
  }

  public static Context with(Context context, Span consumerSpan) {
    return SpanKey.CONSUMER.storeInContext(context, consumerSpan);
  }

  private ConsumerSpan() {}
}
