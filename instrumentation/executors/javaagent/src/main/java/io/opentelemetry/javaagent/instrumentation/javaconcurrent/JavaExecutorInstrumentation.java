/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.opentelemetry.javaagent.instrumentation.javaconcurrent;

import static net.bytebuddy.matcher.ElementMatchers.nameMatches;
import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.takesArgument;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;

import io.opentelemetry.context.Context;
import io.opentelemetry.javaagent.extension.instrumentation.TypeTransformer;
import io.opentelemetry.javaagent.instrumentation.api.ContextStore;
import io.opentelemetry.javaagent.instrumentation.api.InstrumentationContext;
import io.opentelemetry.javaagent.instrumentation.api.Java8BytecodeBridge;
import io.opentelemetry.javaagent.instrumentation.api.concurrent.CallableWrapper;
import io.opentelemetry.javaagent.instrumentation.api.concurrent.ExecutorAdviceHelper;
import io.opentelemetry.javaagent.instrumentation.api.concurrent.PropagatedContext;
import io.opentelemetry.javaagent.instrumentation.api.concurrent.RunnableWrapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;
import net.bytebuddy.asm.Advice;

public class JavaExecutorInstrumentation extends AbstractExecutorInstrumentation {

  @Override
  public void transform(TypeTransformer transformer) {
    transformer.applyAdviceToMethod(
        named("execute").and(takesArgument(0, Runnable.class)).and(takesArguments(1)),
        JavaExecutorInstrumentation.class.getName() + "$SetExecuteRunnableStateAdvice");
    // Netty uses addTask as the actual core of their submission; there are non-standard variations
    // like execute(Runnable,boolean) that aren't caught by standard instrumentation
    transformer.applyAdviceToMethod(
        named("addTask").and(takesArgument(0, Runnable.class)).and(takesArguments(1)),
        JavaExecutorInstrumentation.class.getName() + "$SetExecuteRunnableStateAdvice");
    transformer.applyAdviceToMethod(
        named("execute").and(takesArgument(0, ForkJoinTask.class)),
        JavaExecutorInstrumentation.class.getName() + "$SetJavaForkJoinStateAdvice");
    transformer.applyAdviceToMethod(
        named("submit").and(takesArgument(0, Runnable.class)),
        JavaExecutorInstrumentation.class.getName() + "$SetSubmitRunnableStateAdvice");
    transformer.applyAdviceToMethod(
        named("submit").and(takesArgument(0, Callable.class)),
        JavaExecutorInstrumentation.class.getName() + "$SetCallableStateAdvice");
    transformer.applyAdviceToMethod(
        named("submit").and(takesArgument(0, ForkJoinTask.class)),
        JavaExecutorInstrumentation.class.getName() + "$SetJavaForkJoinStateAdvice");
    transformer.applyAdviceToMethod(
        nameMatches("invoke(Any|All)$").and(takesArgument(0, Collection.class)),
        JavaExecutorInstrumentation.class.getName()
            + "$SetCallableStateForCallableCollectionAdvice");
    transformer.applyAdviceToMethod(
        nameMatches("invoke").and(takesArgument(0, ForkJoinTask.class)),
        JavaExecutorInstrumentation.class.getName() + "$SetJavaForkJoinStateAdvice");
    transformer.applyAdviceToMethod(
        named("schedule").and(takesArgument(0, Runnable.class)),
        JavaExecutorInstrumentation.class.getName() + "$SetSubmitRunnableStateAdvice");
    transformer.applyAdviceToMethod(
        named("schedule").and(takesArgument(0, Callable.class)),
        JavaExecutorInstrumentation.class.getName() + "$SetCallableStateAdvice");
  }

  @SuppressWarnings("unused")
  public static class SetExecuteRunnableStateAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static PropagatedContext enterJobSubmit(
        @Advice.Argument(value = 0, readOnly = false) Runnable task) {
      Context context = Java8BytecodeBridge.currentContext();
      if (ExecutorAdviceHelper.shouldPropagateContext(context, task)) {
        task = RunnableWrapper.wrapIfNeeded(task);
        ContextStore<Runnable, PropagatedContext> contextStore =
            InstrumentationContext.get(Runnable.class, PropagatedContext.class);
        return ExecutorAdviceHelper.attachContextToTask(context, contextStore, task);
      }
      return null;
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
    public static void exitJobSubmit(
        @Advice.Enter PropagatedContext propagatedContext, @Advice.Thrown Throwable throwable) {
      ExecutorAdviceHelper.cleanUpAfterSubmit(propagatedContext, throwable);
    }
  }

  @SuppressWarnings("unused")
  public static class SetJavaForkJoinStateAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static PropagatedContext enterJobSubmit(
        @Advice.Argument(value = 0, readOnly = false) ForkJoinTask<?> task) {
      Context context = Java8BytecodeBridge.currentContext();
      if (ExecutorAdviceHelper.shouldPropagateContext(context, task)) {
        ContextStore<ForkJoinTask<?>, PropagatedContext> contextStore =
            InstrumentationContext.get(ForkJoinTask.class, PropagatedContext.class);
        return ExecutorAdviceHelper.attachContextToTask(context, contextStore, task);
      }
      return null;
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
    public static void exitJobSubmit(
        @Advice.Enter PropagatedContext propagatedContext, @Advice.Thrown Throwable throwable) {
      ExecutorAdviceHelper.cleanUpAfterSubmit(propagatedContext, throwable);
    }
  }

  @SuppressWarnings("unused")
  public static class SetSubmitRunnableStateAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static PropagatedContext enterJobSubmit(
        @Advice.Argument(value = 0, readOnly = false) Runnable task) {
      Context context = Java8BytecodeBridge.currentContext();
      if (ExecutorAdviceHelper.shouldPropagateContext(context, task)) {
        task = RunnableWrapper.wrapIfNeeded(task);
        ContextStore<Runnable, PropagatedContext> contextStore =
            InstrumentationContext.get(Runnable.class, PropagatedContext.class);
        return ExecutorAdviceHelper.attachContextToTask(context, contextStore, task);
      }
      return null;
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
    public static void exitJobSubmit(
        @Advice.Enter PropagatedContext propagatedContext,
        @Advice.Thrown Throwable throwable,
        @Advice.Return Future<?> future) {
      if (propagatedContext != null && future != null) {
        ContextStore<Future<?>, PropagatedContext> contextStore =
            InstrumentationContext.get(Future.class, PropagatedContext.class);
        contextStore.put(future, propagatedContext);
      }
      ExecutorAdviceHelper.cleanUpAfterSubmit(propagatedContext, throwable);
    }
  }

  @SuppressWarnings("unused")
  public static class SetCallableStateAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static PropagatedContext enterJobSubmit(
        @Advice.Argument(value = 0, readOnly = false) Callable<?> task) {
      Context context = Java8BytecodeBridge.currentContext();
      if (ExecutorAdviceHelper.shouldPropagateContext(context, task)) {
        task = CallableWrapper.wrapIfNeeded(task);
        ContextStore<Callable<?>, PropagatedContext> contextStore =
            InstrumentationContext.get(Callable.class, PropagatedContext.class);
        return ExecutorAdviceHelper.attachContextToTask(context, contextStore, task);
      }
      return null;
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
    public static void exitJobSubmit(
        @Advice.Enter PropagatedContext propagatedContext,
        @Advice.Thrown Throwable throwable,
        @Advice.Return Future<?> future) {
      if (propagatedContext != null && future != null) {
        ContextStore<Future<?>, PropagatedContext> contextStore =
            InstrumentationContext.get(Future.class, PropagatedContext.class);
        contextStore.put(future, propagatedContext);
      }
      ExecutorAdviceHelper.cleanUpAfterSubmit(propagatedContext, throwable);
    }
  }

  @SuppressWarnings("unused")
  public static class SetCallableStateForCallableCollectionAdvice {

    @Advice.OnMethodEnter(suppress = Throwable.class)
    public static Collection<?> submitEnter(
        @Advice.Argument(value = 0, readOnly = false) Collection<? extends Callable<?>> tasks) {
      if (tasks == null) {
        return Collections.emptyList();
      }

      Collection<Callable<?>> wrappedTasks = new ArrayList<>(tasks.size());
      Context context = Java8BytecodeBridge.currentContext();
      for (Callable<?> task : tasks) {
        if (ExecutorAdviceHelper.shouldPropagateContext(context, task)) {
          Callable<?> newTask = CallableWrapper.wrapIfNeeded(task);
          wrappedTasks.add(newTask);
          ContextStore<Callable<?>, PropagatedContext> contextStore =
              InstrumentationContext.get(Callable.class, PropagatedContext.class);
          ExecutorAdviceHelper.attachContextToTask(context, contextStore, newTask);
        } else {
          // note that task may be null here
          wrappedTasks.add(task);
        }
      }
      tasks = wrappedTasks;
      // returning tasks and not propagatedContexts to avoid allocating another list just for an
      // edge case (exception)
      return tasks;
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class, suppress = Throwable.class)
    public static void submitExit(
        @Advice.Enter Collection<? extends Callable<?>> wrappedTasks,
        @Advice.Thrown Throwable throwable) {
      /*
       Note1: invokeAny doesn't return any futures so all we need to do for it
       is to make sure we close all scopes in case of an exception.
       Note2: invokeAll does return futures - but according to its documentation
       it actually only returns after all futures have been completed - i.e. it blocks.
       This means we do not need to setup any hooks on these futures, we just need to clear
       any parent spans in case of an error.
       (according to ExecutorService docs and AbstractExecutorService code)
      */
      if (throwable != null) {
        for (Callable<?> task : wrappedTasks) {
          if (task != null) {
            ContextStore<Callable<?>, PropagatedContext> contextStore =
                InstrumentationContext.get(Callable.class, PropagatedContext.class);
            PropagatedContext propagatedContext = contextStore.get(task);
            ExecutorAdviceHelper.cleanUpAfterSubmit(propagatedContext, throwable);
          }
        }
      }
    }
  }
}
