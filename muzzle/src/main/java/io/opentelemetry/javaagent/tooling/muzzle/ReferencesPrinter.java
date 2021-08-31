/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.tooling.muzzle;

import static java.lang.System.lineSeparator;

import io.opentelemetry.javaagent.extension.instrumentation.InstrumentationModule;
import io.opentelemetry.javaagent.extension.muzzle.ClassRef;
import io.opentelemetry.javaagent.extension.muzzle.FieldRef;
import io.opentelemetry.javaagent.extension.muzzle.MethodRef;
import io.opentelemetry.javaagent.extension.muzzle.Source;
import java.util.ServiceLoader;

@SuppressWarnings("SystemOut")
public final class ReferencesPrinter {

  private static final String INDENT = "  ";

  /**
   * For all {@link InstrumentationModule}s found in the current thread's context classloader this
   * method prints references returned by the {@link InstrumentationModule#getMuzzleReferences()}
   * method to the standard output.
   */
  public static void printMuzzleReferences() {
    for (InstrumentationModule instrumentationModule :
        ServiceLoader.load(InstrumentationModule.class)) {
      try {
        System.out.println(instrumentationModule.getClass().getName());
        for (ClassRef ref : instrumentationModule.getMuzzleReferences().values()) {
          System.out.print(prettyPrint(ref));
        }
      } catch (RuntimeException e) {
        String message =
            "Unexpected exception printing references for "
                + instrumentationModule.getClass().getName();
        System.out.println(message);
        throw new IllegalStateException(message, e);
      }
    }
  }

  private static String prettyPrint(ClassRef ref) {
    StringBuilder builder = new StringBuilder(INDENT).append(ref).append(lineSeparator());
    if (!ref.getSources().isEmpty()) {
      builder.append(INDENT).append(INDENT).append("Sources:").append(lineSeparator());
      for (Source source : ref.getSources()) {
        builder
            .append(INDENT)
            .append(INDENT)
            .append(INDENT)
            .append("at: ")
            .append(source)
            .append(lineSeparator());
      }
    }
    for (FieldRef field : ref.getFields()) {
      builder.append(INDENT).append(INDENT).append(field).append(lineSeparator());
    }
    for (MethodRef method : ref.getMethods()) {
      builder.append(INDENT).append(INDENT).append(method).append(lineSeparator());
    }
    return builder.toString();
  }

  private ReferencesPrinter() {}
}
