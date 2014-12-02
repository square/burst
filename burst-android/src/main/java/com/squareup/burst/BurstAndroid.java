package com.squareup.burst;

import android.app.Instrumentation;
import android.content.Context;
import android.test.AndroidTestRunner;
import java.io.File;
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
    if (instrumentation == null) {
      throw new IllegalStateException("setInstrumentation not called.");
    }
    if (!(test instanceof TestSuite)) {
      throw new IllegalArgumentException("Expected instance of TestSuite.");
    }

    TestSuite exploded = new TestSuite();
    try {
      explodeSuite((TestSuite) test, exploded);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    super.setTest(exploded);
  }

  /**
   * Callback for determining whether or not a test class is applicable to run. This can be used
   * for filtering which classes run based on properties not known directly to the runner.
   * <p>
   * For example, certain test classes are only appropriate to run on devices that you consider
   * a tablet. A {@code @Tablet} annotation can be created and placed on these classes. Subclasses
   * of this method could then query as to whether the device matched the requirements if the
   * annotation was present.
   * <pre>{@code
   * &#064;Override
   * public boolean isClassApplicable(Class&lt;?> cls) {
   *   return cls.getAnnotation(Tablet.class) == null
   *       || deviceIsConsideredTablet();
   * }
   * }</pre>
   */
  @SuppressWarnings("UnusedParameters") // Parameters are for subclass usage.
  public boolean isClassApplicable(Class<?> cls) {
    return true;
  }

  /**
   * Callback for determining whether or not a test method is applicable to run. This can be used
   * for filtering which methods run based on properties not known directly to the runner.
   * <p>
   * For example, certain test methods are only appropriate to run on devices that you consider
   * a tablet. A {@code @Tablet} annotation can be created and placed on these methods. Subclasses
   * of this method could then query as to whether the device matched the requirements if the
   * annotation was present.
   * <pre>{@code
   * &#064;Override
   * public boolean isMethodApplicable(Class&lt;?> cls, Method method) {
   *   return method.getAnnotation(Tablet.class) == null
   *       || deviceIsConsideredTablet();
   * }
   * }</pre>
   */
  @SuppressWarnings("UnusedParameters") // Parameters are for subclass usage.
  public boolean isMethodApplicable(Class<?> cls, Method method) {
    return true;
  }

  private void explodeSuite(TestSuite suite, TestSuite exploded) throws Exception {
    Context targetContext = instrumentation.getTargetContext();
    ClassLoader classLoader = targetContext.getClassLoader();
    Class<?> testClass = classLoader.loadClass(suite.getName());
    if (!isClassApplicable(testClass)) {
      return;
    }

    @SuppressWarnings("unchecked") Enumeration<Test> testEnumerator = suite.tests();
    while (testEnumerator.hasMoreElements()) {
      Test test = testEnumerator.nextElement();
      if (test instanceof TestCase) {
        Constructor<?> constructor = BurstableConstructor.findSingle(testClass);
        Enum<?>[][] constructorArgsList = Burst.explodeArguments(constructor);

        TestCase testCase = (TestCase) test;
        Method method = testClass.getMethod(testCase.getName());
        if (!isMethodApplicable(testClass, method)) {
          continue;
        }

        for (Enum<?>[] methodArgs : Burst.explodeArguments(method)) {
          // Loop constructor args last so we only iterate and explode each test method once.
          for (Enum<?>[] constructorArgs : constructorArgsList) {
            String name = nameWithArguments(method.getName(), constructorArgs, methodArgs);
            // We can't call setName(name) - that would break TestCase's runTest which reflectively
            // invokes methods by name. Instead we generate a new class which overrides getName.
            // The override will return the original method name while runTest is executing.
            File dexCache = targetContext.getDir("dx", Context.MODE_PRIVATE);
            TestCase instrumentedTestCase = TestInstrumenter.instrumentTestCase(testClass,
                constructor.getParameterTypes(), constructorArgs, name, dexCache);
            instrumentedTestCase.setName(method.getName());
            exploded.addTest(instrumentedTestCase);
          }
        }
      } else if (test instanceof TestSuite) {
        explodeSuite((TestSuite) test, exploded); // Recursively explode this suite's tests.
      } else {
        throw new IllegalStateException(
            "Unknown Test type. Not TestCase or TestSuite. " + test.getClass().getName());
      }
    }
  }

  private static String nameWithArguments(String name, Enum<?>[] constructorArgs,
      Enum<?>[] methodArgs) {
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
