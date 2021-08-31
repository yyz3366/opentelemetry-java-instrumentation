/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

import static io.opentelemetry.api.trace.SpanKind.CLIENT
import static io.opentelemetry.api.trace.SpanKind.INTERNAL

import io.opentelemetry.semconv.trace.attributes.SemanticAttributes
import javax.persistence.EntityManager
import javax.persistence.EntityManagerFactory
import javax.persistence.EntityTransaction
import javax.persistence.LockModeType
import javax.persistence.Persistence
import javax.persistence.Query
import spock.lang.Shared
import spock.lang.Unroll

class EntityManagerTest extends AbstractHibernateTest {

  @Shared
  EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("test-pu")

  @Unroll
  def "test hibernate action #testName"() {
    setup:
    EntityManager entityManager = entityManagerFactory.createEntityManager()
    EntityTransaction entityTransaction = entityManager.getTransaction()
    entityTransaction.begin()

    def entity = prepopulated.get(0)
    if (attach) {
      entity = runWithSpan("setup") {
        entityManager.merge(prepopulated.get(0))
      }
      ignoreTracesAndClear(1)
    }

    when:
    try {
      sessionMethodTest.call(entityManager, entity)
    } catch (Exception e) {
      // We expected this, we should see the error field set on the span.
    }

    entityTransaction.commit()
    entityManager.close()

    then:
    boolean isPersistTest = "persist" == testName
    assertTraces(1) {
      trace(0, 4 + (isPersistTest ? 1 : 0)) {
        span(0) {
          name "Session"
          kind INTERNAL
          hasNoParent()
          attributes {
          }
        }
        span(1) {
          name ~/Session.$methodName $resource/
          kind INTERNAL
          childOf span(0)
          attributes {
          }
        }

        def offset = 0
        if (isPersistTest) {
          // persist test has an extra query for getting id of inserted element
          offset = 1
          span(2) {
            name "SELECT db1.Value"
            childOf span(1)
            kind CLIENT
            attributes {
              "${SemanticAttributes.DB_SYSTEM.key}" "h2"
              "${SemanticAttributes.DB_NAME.key}" "db1"
              "${SemanticAttributes.DB_USER.key}" "sa"
              "${SemanticAttributes.DB_CONNECTION_STRING.key}" "h2:mem:"
              "${SemanticAttributes.DB_STATEMENT.key}" String
              "${SemanticAttributes.DB_OPERATION.key}" String
              "${SemanticAttributes.DB_SQL_TABLE.key}" "Value"
            }
          }
        }

        if (!flushOnCommit) {
          span(2 + offset) {
            childOf span(1)
            kind CLIENT
            attributes {
              "${SemanticAttributes.DB_SYSTEM.key}" "h2"
              "${SemanticAttributes.DB_NAME.key}" "db1"
              "${SemanticAttributes.DB_USER.key}" "sa"
              "${SemanticAttributes.DB_CONNECTION_STRING.key}" "h2:mem:"
              "${SemanticAttributes.DB_STATEMENT.key}" String
              "${SemanticAttributes.DB_OPERATION.key}" String
              "${SemanticAttributes.DB_SQL_TABLE.key}" "Value"
            }
          }
          span(3 + offset) {
            name "Transaction.commit"
            kind INTERNAL
            childOf span(0)
            attributes {
            }
          }
        } else {
          span(2 + offset) {
            name "Transaction.commit"
            kind INTERNAL
            childOf span(0)
            attributes {
            }
          }
          span(3 + offset) {
            childOf span(2 + offset)
            kind CLIENT
            attributes {
              "${SemanticAttributes.DB_SYSTEM.key}" "h2"
              "${SemanticAttributes.DB_NAME.key}" "db1"
              "${SemanticAttributes.DB_USER.key}" "sa"
              "${SemanticAttributes.DB_CONNECTION_STRING.key}" "h2:mem:"
              "${SemanticAttributes.DB_STATEMENT.key}" String
              "${SemanticAttributes.DB_OPERATION.key}" String
              "${SemanticAttributes.DB_SQL_TABLE.key}" "Value"
            }
          }
        }
      }
    }

    where:
    testName                                  | methodName   | resource | attach | flushOnCommit | sessionMethodTest
    "lock"                                    | "lock"       | "Value"  | true   | false         | { em, val ->
      em.lock(val, LockModeType.PESSIMISTIC_READ)
    }
    "refresh"                                 | "refresh"    | "Value"  | true   | false         | { em, val ->
      em.refresh(val)
    }
    "find"                                    | "(get|find)" | "Value"  | false  | false         | { em, val ->
      em.find(Value, val.getId())
    }
    "persist"                                 | "persist"    | "Value"  | false  | true          | { em, val ->
      em.persist(new Value("insert me"))
    }
    "merge"                                   | "merge"      | "Value"  | true   | true          | { em, val ->
      val.setName("New name")
      em.merge(val)
    }
    "remove"                                  | "delete"     | "Value"  | true   | true          | { em, val ->
      em.remove(val)
    }
  }

  @Unroll
  def "test attaches State to query created via #queryMethodName"() {
    setup:
    EntityManager entityManager = entityManagerFactory.createEntityManager()
    EntityTransaction entityTransaction = entityManager.getTransaction()
    entityTransaction.begin()
    Query query = queryBuildMethod(entityManager)
    query.getResultList()
    entityTransaction.commit()
    entityManager.close()

    expect:
    assertTraces(1) {
      trace(0, 4) {
        span(0) {
          name "Session"
          kind INTERNAL
          hasNoParent()
          attributes {
          }
        }
        span(1) {
          name resource
          kind INTERNAL
          childOf span(0)
          attributes {
          }
        }
        span(2) {
          name "SELECT db1.Value"
          kind CLIENT
          childOf span(1)
          attributes {
            "${SemanticAttributes.DB_SYSTEM.key}" "h2"
            "${SemanticAttributes.DB_NAME.key}" "db1"
            "${SemanticAttributes.DB_USER.key}" "sa"
            "${SemanticAttributes.DB_CONNECTION_STRING.key}" "h2:mem:"
            "${SemanticAttributes.DB_STATEMENT.key}" String
            "${SemanticAttributes.DB_OPERATION.key}" "SELECT"
            "${SemanticAttributes.DB_SQL_TABLE.key}" "Value"
          }
        }
        span(3) {
          name "Transaction.commit"
          kind INTERNAL
          childOf span(0)
          attributes {
          }
        }
      }
    }

    where:
    queryMethodName  | resource       | queryBuildMethod
    "createQuery"    | "SELECT Value" | { em -> em.createQuery("from Value") }
    "getNamedQuery"  | "SELECT Value" | { em -> em.createNamedQuery("TestNamedQuery") }
    "createSQLQuery" | "SELECT Value" | { em -> em.createNativeQuery("SELECT * FROM Value") }
  }

}
