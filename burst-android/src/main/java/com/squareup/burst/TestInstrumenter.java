package com.squareup.burst;

import android.util.Log;
import com.google.dexmaker.stock.ProxyBuilder;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicBoolean;
import junit.framework.TestCase;

final class TestInstrumenter {
  /**
   * Instruments a test case, overriding {@link TestCase#getName()} to return an arbitrary name.
   * This can be used to embed variation values, or any other information, in the test name that
   * will be displayed.
   *
   * @param testClass the class of the test to instrument
   * @param constructorArgTypes the parameter types of the test class's burstable constructor
   * @param constructorArgValues the arguments to be passed to the burstable constructor
   * @param name a descriptive name for the test, used for presentation
   * @param dexCache a location where dex files can be written
   * @return the instrumented test case
   */
  public static TestCase instrumentTestCase(final Class<?> testClass,
      Class<?>[] constructorArgTypes, Object[] constructorArgValues, final String name,
      File dexCache) {
    InvocationHandler handler = new InvocationHandler() {
      final AtomicBoolean inRunTest = new AtomicBoolean();

      @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        switch (method.getName()) {
          case "runTest":
            inRunTest.set(true);
            ProxyBuilder.callSuper(proxy, method, args);
            inRunTest.set(false);
            return null;
          case "getName":
            // Normally we want getName() to return our descriptive test name, but if runTest() is
            // executing, we must return the plain method name since it may be invoked reflectively.
            if (!inRunTest.get()) {
              return name;
            }
            break;
          case "scrubClass":
            scrub(testClass, (TestCase) proxy);
            return null;
        }
        return ProxyBuilder.callSuper(proxy, method, args);
      }
    };

    try {
      return (TestCase) ProxyBuilder.forClass(testClass)
          .handler(handler)
          .dexCache(dexCache)
          .constructorArgTypes(constructorArgTypes)
          .constructorArgValues(constructorArgValues)
          .build();
    } catch (IOException e) {
      throw new RuntimeException("Instrumentation failed.", e);
    }
  }

  /**
   * Like {@link android.test.ActivityTestCase#scrubClass(Class)}, but only looks at fields declared
   * in the test class. This is to avoid touching fields added by dexmaker in the proxy class.
   */
  private static void scrub(Class<?> testClass, TestCase testCase) throws IllegalAccessException {
    for (Field field : testClass.getDeclaredFields()) {
      if (!field.getType().isPrimitive() && (field.getModifiers() & Modifier.FINAL) == 0) {
        try {
          field.setAccessible(true);
          field.set(testCase, null);
        } catch (Exception e) {
          Log.e("TestInstrumenter", "Error: Could not nullify field!");
        }

        if (field.get(testCase) != null) {
          Log.e("TestInstrumenter", "Error: Could not nullify field!");
        }
      }
    }
  }

  private TestInstrumenter() {
    throw new AssertionError("No instances.");
  }
}
