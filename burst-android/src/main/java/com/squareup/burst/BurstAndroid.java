package com.squareup.burst;

import android.test.AndroidTestRunner;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Enumeration;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class BurstAndroid extends AndroidTestRunner {
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
    ClassLoader classLoader = testSuite.getClass().getClassLoader();

    Class<?> testClass = classLoader.loadClass(testSuite.getName());
    Constructor<?> constructor = Burst.findConstructor(testClass);

    for (Object[] constructorArgs : Burst.explodeArguments(constructor)) {
      @SuppressWarnings("unchecked") Enumeration<Test> testEnumerator = testSuite.tests();
      while (testEnumerator.hasMoreElements()) {
        Test test = testEnumerator.nextElement();
        if (test instanceof TestCase) {
          TestCase testCase = (TestCase) test;
          Method method = testClass.getMethod(testCase.getName());
          for (Object[] methodArgs : Burst.explodeArguments(method)) {
            String name = Burst.explodedName(method.getName(), methodArgs);
            result.addTest(
                new BurstTestCase(name, constructor, constructorArgs, method, methodArgs));
          }
        } else if (test instanceof TestSuite) {
          explodeSuite((TestSuite) test, result); // Recursively explode this suite's tests.
        } else {
          throw new IllegalStateException(
              "Unknown Test type. Not TestCase or TestSuite. " + test.getClass().getName());
        }
      }
    }
  }
}
