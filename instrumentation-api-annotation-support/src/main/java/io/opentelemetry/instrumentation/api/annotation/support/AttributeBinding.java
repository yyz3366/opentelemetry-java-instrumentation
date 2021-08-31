/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.api.annotation.support;

import io.opentelemetry.instrumentation.api.tracer.AttributeSetter;

/** Represents the binding of a method parameter to an attribute of a traced method. */
@FunctionalInterface
public interface AttributeBinding {

  /**
   * Applies the value of the method argument as an attribute on the span for the traced method.
   *
   * @param setter the {@link AttributeSetter} onto which to add the attribute
   * @param arg the value of the method argument
   */
  void apply(AttributeSetter setter, Object arg);
}
