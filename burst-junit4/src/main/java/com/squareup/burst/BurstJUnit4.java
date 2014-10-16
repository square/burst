package com.squareup.burst;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

import static com.squareup.burst.Util.checkNotNull;
import static java.util.Collections.unmodifiableList;

/**
 * A suite associated with a particular test class. Its children are {@link BurstRunner}s, each
 * representing a particular variation of that class.
 */
public final class BurstJUnit4 extends Suite {
  public BurstJUnit4(Class<?> cls) throws InitializationError {
    super(cls, explode(cls));
  }

  /**
   * ParentRunner's default filter implementation generates a hierarchy of test descriptions,
   * applies the filter to those descriptions, and removes any test nodes whose descriptions were
   * all filtered out.
   * <p>
   * This would be problematic for us since we generate non-standard test descriptions which include
   * parameter information. This implementation lets each {@link BurstRunner} child filter itself
   * via {@link BurstRunner#filter(Filter)}.
   */
  @Override public void filter(Filter filter) throws NoTestsRemainException {
    List<Runner> filteredChildren = ParentRunnerSpy.getFilteredChildren(this);
    // Iterate over a clone so that we can safely mutate the original.
    for (Runner child : new ArrayList<>(filteredChildren)) {
      try {
        filter.apply(child);
      } catch (NoTestsRemainException e) {
        filteredChildren.remove(child);
      }
    }
    if (filteredChildren.isEmpty()) {
      throw new NoTestsRemainException();
    }
  }

  static List<Runner> explode(Class<?> cls) throws InitializationError {
    checkNotNull(cls, "cls");

    TestClass testClass = new TestClass(cls);
    List<FrameworkMethod> testMethods = testClass.getAnnotatedMethods(Test.class);

    List<FrameworkMethod> burstMethods = new ArrayList<>(testMethods.size());
    for (FrameworkMethod testMethod : testMethods) {
      Method method = testMethod.getMethod();
      for (Enum<?>[] methodArgs : Burst.explodeArguments(method)) {
        burstMethods.add(new BurstMethod(method, methodArgs));
      }
    }

    Constructor<?> constructor = findConstructor(cls);
    Enum<?>[][] constructorArgsList = Burst.explodeArguments(constructor);
    List<Runner> burstRunners = new ArrayList<>(constructorArgsList.length);
    for (Enum<?>[] constructorArgs : constructorArgsList) {
      burstRunners.add(new BurstRunner(cls, constructor, constructorArgs, burstMethods));
    }

    return unmodifiableList(burstRunners);
  }

  /** Locate the default or public constructor for a test class. */
  static Constructor<?> findConstructor(Class<?> cls) {
    checkNotNull(cls, "cls");

    Constructor<?>[] constructors = cls.getConstructors();
    if (constructors.length == 1) {
      return constructors[0];
    }
    throw new IllegalStateException(cls.getName() + " requires a single public constructor.");
  }

  static String nameWithArguments(String name, Enum<?>[] arguments) {
    if (arguments.length == 0) {
      return name;
    }
    return name + '[' + Burst.friendlyName(arguments) + ']';
  }
}
