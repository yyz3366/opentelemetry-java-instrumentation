/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.tomcat.v7_0

import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.ERROR
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.EXCEPTION
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.INDEXED_CHILD
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.QUERY_PARAM
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.REDIRECT
import static io.opentelemetry.instrumentation.test.base.HttpServerTest.ServerEndpoint.SUCCESS

import groovy.servlet.AbstractHttpServlet
import io.opentelemetry.instrumentation.test.base.HttpServerTest
import java.util.concurrent.CountDownLatch
import javax.servlet.annotation.WebServlet
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@WebServlet(asyncSupported = true)
class AsyncServlet extends AbstractHttpServlet {
  @Override
  protected void service(HttpServletRequest req, HttpServletResponse resp) {
    HttpServerTest.ServerEndpoint endpoint = HttpServerTest.ServerEndpoint.forPath(req.servletPath)
    def latch = new CountDownLatch(1)
    def context = req.startAsync()
    context.start {
      try {
        HttpServerTest.controller(endpoint) {
          resp.contentType = "text/plain"
          switch (endpoint) {
            case SUCCESS:
              resp.status = endpoint.status
              resp.writer.print(endpoint.body)
              context.complete()
              break
            case INDEXED_CHILD:
              endpoint.collectSpanAttributes { req.getParameter(it) }
              resp.status = endpoint.status
              context.complete()
              break
            case QUERY_PARAM:
              resp.status = endpoint.status
              resp.writer.print(req.queryString)
              context.complete()
              break
            case REDIRECT:
              resp.sendRedirect(endpoint.body)
              context.complete()
              break
            case ERROR:
              resp.status = endpoint.status
              resp.writer.print(endpoint.body)
              context.complete()
              break
            case EXCEPTION:
              resp.status = endpoint.status
              resp.writer.print(endpoint.body)
              context.complete()
              throw new Exception(endpoint.body)
          }
        }
      } finally {
        latch.countDown()
      }
    }
    latch.await()
  }
}
