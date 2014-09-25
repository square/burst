package com.squareup.burst;

import java.lang.reflect.Method;
import org.junit.internal.runners.model.ReflectiveCallable;
import org.junit.runners.model.FrameworkMethod;

import static com.squareup.burst.BurstJUnit4.nameWithArguments;
import static com.squareup.burst.Util.checkNotNull;

final class BurstMethod extends FrameworkMethod {
  private final Enum<?>[] methodArgs;

  BurstMethod(Method method, Enum<?>[] methodArgs) {
    super(checkNotNull(method, "method"));
    this.methodArgs = checkNotNull(methodArgs, "methodArgs");
  }

  @Override public Object invokeExplosively(final Object target, Object... params)
      throws Throwable {
    checkNotNull(target, "target");

    ReflectiveCallable callable = new ReflectiveCallable() {
      @Override protected Object runReflectiveCall() throws Throwable {
        return getMethod().invoke(target, methodArgs);
      }
    };
    return callable.run();
  }

  @Override public String getName() {
    return nameWithArguments(super.getName(), methodArgs);
  }
}
