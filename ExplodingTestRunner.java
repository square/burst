package com.squareup.instrumentation;

import android.app.Instrumentation;
import android.test.AndroidTestRunner;
import com.squareup.ShowTabletUi;
import com.squareup.instrumentation.constraints.PhoneOnly;
import com.squareup.instrumentation.constraints.TabletOnly;
import java.lang.reflect.Method;
import java.util.Enumeration;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import static com.squareup.instrumentation.TestSuiteUtils.explodeTest;

/**
 * Our own test runner which explodes tests annotated with @Variations.
 */
public class ExplodingTestRunner extends AndroidTestRunner {
  private final Instrumentation instrumentation;

  public ExplodingTestRunner(Instrumentation instrumentation) {
    this.instrumentation = instrumentation;
  }

  @Override
  public void setInstrumentation(Instrumentation instr) {
    super.setInstrumentation(instrumentation);
  }

  @Override
  public void setInstrumentaiton(Instrumentation instr) {
    super.setInstrumentation(instrumentation);
  }

  @Override public void setTest(Test test) {
    if (!(test instanceof TestSuite)) {
      throw new UnsupportedOperationException("Expected instance of TestSuite.");
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
    boolean isTablet = instrumentation.getTargetContext().getResources().getBoolean(ShowTabletUi.ID);
    ClassLoader classLoader = instrumentation.getTargetContext().getClassLoader();
    Class<?> testCaseClass = classLoader.loadClass(testSuite.getName());
    Enumeration<Test> testEnumerator = testSuite.tests();
    while (testEnumerator.hasMoreElements()) {
      Test test = testEnumerator.nextElement();
      if (test instanceof TestCase) {
        TestCase testCase = (TestCase) test;
        Method method = testCaseClass.getMethod(testCase.getName());
        if (!shouldSkipMethod(method, isTablet)) {
          result.addTest(explodeTest(testCaseClass, method, isTablet));
        }
      } else {
        explodeSuite((TestSuite) test, result); // Recursively explode this suite's tests.
      }
    }
  }

  private boolean shouldSkipMethod(Method method, boolean isTablet) {
    boolean tabletOnly = method.isAnnotationPresent(TabletOnly.class)
        || method.getDeclaringClass().isAnnotationPresent(TabletOnly.class);
    boolean phoneOnly = method.isAnnotationPresent(PhoneOnly.class)
        || method.getDeclaringClass().isAnnotationPresent(PhoneOnly.class);
    return (phoneOnly && isTablet) || (tabletOnly && !isTablet);
  }
}
