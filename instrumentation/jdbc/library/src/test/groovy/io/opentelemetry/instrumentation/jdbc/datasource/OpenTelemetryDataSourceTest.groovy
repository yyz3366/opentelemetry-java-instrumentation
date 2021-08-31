/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.instrumentation.jdbc.datasource

import io.opentelemetry.instrumentation.jdbc.internal.OpenTelemetryConnection
import spock.lang.Specification

class OpenTelemetryDataSourceTest extends Specification {

  def "verify get connection"() {
    when:
    def dataSource = new OpenTelemetryDataSource(new TestDataSource())
    def connection = dataSource.getConnection()

    then:
    connection != null
    connection instanceof OpenTelemetryConnection

    when:
    def dbInfo = ((OpenTelemetryConnection) connection).dbInfo

    then:
    dbInfo.system == "postgresql"
    dbInfo.subtype == null
    dbInfo.shortUrl == "postgresql://127.0.0.1:5432"
    dbInfo.user == null
    dbInfo.name == null
    dbInfo.db == "dbname"
    dbInfo.host == "127.0.0.1"
    dbInfo.port == 5432
  }

  def "verify get connection with username and password"() {
    when:
    def dataSource = new OpenTelemetryDataSource(new TestDataSource())
    def connection = dataSource.getConnection(null, null)

    then:
    connection != null
    connection instanceof OpenTelemetryConnection

    when:
    def dbInfo = ((OpenTelemetryConnection) connection).dbInfo

    then:
    dbInfo.system == "postgresql"
    dbInfo.subtype == null
    dbInfo.shortUrl == "postgresql://127.0.0.1:5432"
    dbInfo.user == null
    dbInfo.name == null
    dbInfo.db == "dbname"
    dbInfo.host == "127.0.0.1"
    dbInfo.port == 5432
  }

}
