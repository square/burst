package com.squareup.burst;

import com.google.dexmaker.stock.ProxyBuilder;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import junit.framework.TestCase;

public class TestInstrumenter {
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
  public static TestCase instrumentTestCase(Class<?> testClass, Class<?>[] constructorArgTypes,
      Object[] constructorArgValues, final String name, File dexCache) {
    InvocationHandler handler = new InvocationHandler() {
      @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("getName")) {
          return name;
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
}
