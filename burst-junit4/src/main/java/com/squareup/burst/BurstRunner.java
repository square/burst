package com.squareup.burst;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import static com.squareup.burst.BurstJUnit4.nameWithArguments;
import static com.squareup.burst.Util.checkNotNull;

/**
 * A set of tests associated with a particular variation of some test class.
 */
final class BurstRunner extends BlockJUnit4ClassRunner {
  private final TestConstructor constructor;
  private final Enum<?>[] constructorArgs;
  private final List<FrameworkMethod> methods;

  BurstRunner(Class<?> cls, TestConstructor constructor, Enum<?>[] constructorArgs,
      List<FrameworkMethod> methods) throws InitializationError {
    super(checkNotNull(cls, "cls"));
    this.constructor = checkNotNull(constructor, "constructor");
    this.constructorArgs = checkNotNull(constructorArgs, "constructorArgs");
    this.methods = checkNotNull(methods, "methods");
  }

  @Override protected List<FrameworkMethod> getChildren() {
    return methods;
  }

  @Override protected String getName() {
    return nameWithArguments(super.getName(), constructorArgs,
        constructor.getArgumentAnnotations());
  }

  @Override protected Description describeChild(FrameworkMethod method) {
    return Description.createTestDescription(
        getTestClass().getJavaClass(), getName() + ":" + method.getName(), method.getAnnotations());
  }

  @Override protected Object createTest() throws Exception {
    return constructor.newInstance(constructorArgs);
  }

  @Override protected Statement withBeforeClasses(Statement statement) {
    // The parent runner, BurstJUnit4, will handle @BeforeClass/@AfterClass/@ClassRule once for the
    // whole class. We don't want to repeat them for each variation.
    return statement;
  }

  @Override protected Statement withAfterClasses(Statement statement) {
    // The parent runner, BurstJUnit4, will handle @BeforeClass/@AfterClass/@ClassRule once for the
    // whole class. We don't want to repeat them for each variation.
    return statement;
  }

  @Override protected List<TestRule> classRules() {
    // The parent runner, BurstJUnit4, will handle @BeforeClass/@AfterClass/@ClassRule once for the
    // whole class. We don't want to repeat them for each variation.
    return Collections.emptyList();
  }

  @Override protected void validateConstructor(List<Throwable> errors) {
    // Constructor was already validated by Burst.
  }

  @Override protected void validatePublicVoidNoArgMethods(Class<? extends Annotation> annotation,
      boolean isStatic, List<Throwable> errors) {
    // Methods were already validated by Burst.
  }

  /*
   * ParentRunner's default filter implementation generates a hierarchy of test descriptions,
   * applies the filter to those descriptions, and removes any test nodes whose descriptions were
   * all filtered out.
   * <p>
   * This would be problematic for us since we generate non-standard test descriptions which include
   * parameter information. This implementation generates "plain" descriptions without parameter
   * information and passes those to the filter.
   */
  @Override public void filter(Filter filter) throws NoTestsRemainException {
    List<FrameworkMethod> filteredChildren = ParentRunnerSpy.getFilteredChildren(this);
    // Iterate over a clone so that we can safely mutate the original.
    for (FrameworkMethod child : new ArrayList<>(filteredChildren)) {
      if (!filter.shouldRun(describeChildPlain(child))) {
        filteredChildren.remove(child);
      }
    }
    if (filteredChildren.isEmpty()) {
      throw new NoTestsRemainException();
    }
  }

  /**
   * Generates a "plain" description of a burst test, with no parameter information. This should be
   * used only for filtering. It would not be safe for {@link #describeChild(FrameworkMethod)} to
   * return these plain descriptions, as then multiple tests would share the same description.
   */
  private Description describeChildPlain(FrameworkMethod method) {
    return Description.createTestDescription(getTestClass().getJavaClass(),
        method.getMethod().getName(), method.getAnnotations());
  }
}
