/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.spring.web;

import io.opentelemetry.context.propagation.TextMapSetter;
import org.springframework.http.HttpRequest;

final class HttpRequestSetter implements TextMapSetter<HttpRequest> {
  @Override
  public void set(HttpRequest httpRequest, String key, String value) {
    httpRequest.getHeaders().set(key, value);
  }
}
