package com.squareup.burst;

import android.app.Instrumentation;
import android.test.AndroidTestRunner;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Enumeration;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class BurstAndroid extends AndroidTestRunner {
  private Instrumentation instrumentation;

  @Override public void setInstrumentation(Instrumentation instrumentation) {
    super.setInstrumentation(instrumentation);
    this.instrumentation = instrumentation;
  }

  @Override public void setInstrumentaiton(Instrumentation instrumentation) {
    super.setInstrumentaiton(instrumentation);
    this.instrumentation = instrumentation;
  }

  @Override public void setTest(Test test) {
    if (!(test instanceof TestSuite)) {
      throw new IllegalArgumentException("Expected instance of TestSuite.");
    }

    TestSuite godTestSuite = new TestSuite();
    try {
      explodeSuite((TestSuite) test, godTestSuite);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    super.setTest(godTestSuite);
  }

  private void explodeSuite(TestSuite testSuite, TestSuite result) throws Exception {
    if (instrumentation == null) {
      throw new IllegalStateException("setInstrumentation not called.");
    }

    ClassLoader classLoader = instrumentation.getTargetContext().getClassLoader();
    Class<?> testClass = classLoader.loadClass(testSuite.getName());

    Constructor<?> constructor = Burst.findConstructor(testClass);
    Object[][] constructorArgsList = Burst.explodeArguments(constructor);

    @SuppressWarnings("unchecked") Enumeration<Test> testEnumerator = testSuite.tests();
    while (testEnumerator.hasMoreElements()) {
      Test test = testEnumerator.nextElement();
      if (test instanceof TestCase) {
        TestCase testCase = (TestCase) test;
        Method method = testClass.getMethod(testCase.getName());
        for (Object[] methodArgs : Burst.explodeArguments(method)) {
          // Loop constructor args last so we only iterate and explode each test method once.
          for (Object[] constructorArgs : constructorArgsList) {
            String name = nameWithArguments(method.getName(), constructorArgs, methodArgs);
            result.addTest(
                new BurstTestCase(name, constructor, constructorArgs, method, methodArgs));
          }
        }
      } else if (test instanceof TestSuite) {
        explodeSuite((TestSuite) test, result); // Recursively explode this suite's tests.
      } else {
        throw new IllegalStateException(
            "Unknown Test type. Not TestCase or TestSuite. " + test.getClass().getName());
      }
    }
  }

  private static String nameWithArguments(String name, Object[] constructorArgs,
      Object[] methodArgs) {
    StringBuilder builder = new StringBuilder(name);
    if (constructorArgs.length > 0) {
      builder.append('[').append(Burst.friendlyName(constructorArgs)).append(']');
    }
    if (methodArgs.length > 0) {
      builder.append('[').append(Burst.friendlyName(methodArgs)).append(']');
    }
    return builder.toString();
  }
}
