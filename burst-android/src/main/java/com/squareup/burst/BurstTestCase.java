package com.squareup.burst;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import junit.framework.TestCase;

import static com.squareup.burst.Util.checkNotNull;

final class BurstTestCase extends TestCase {
  private final Constructor<?> constructor;
  private final Object[] constructorArgs;
  private final Method method;
  private final Object[] methodArgs;

  BurstTestCase(String name, Constructor<?> constructor, Object[] constructorArgs, Method method,
      Object[] methodArgs) {
    super(name);
    this.constructor = checkNotNull(constructor, "constructor");
    this.constructorArgs = checkNotNull(constructorArgs, "constructorArgs");
    this.method = checkNotNull(method, "method");
    this.methodArgs = checkNotNull(methodArgs, "methodArgs");
  }

  @Override protected void runTest() throws Throwable {
    try {
      Object instance = constructor.newInstance(constructorArgs);
      method.invoke(instance, methodArgs);
    } catch (InvocationTargetException e) {
      e.fillInStackTrace();
      throw e.getCause();
    } catch (IllegalAccessException e) {
      e.fillInStackTrace();
      throw e;
    }
  }

  @Override public String toString() {
    return getName() + '(' + constructor.getDeclaringClass().getName() + ')';
  }
}
