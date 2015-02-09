package com.squareup.burst;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.runners.ParentRunner;

/**
 * Exposes {@link ParentRunner}'s private members.
 */
final class ParentRunnerSpy {
  private static final Method getFilteredChildrenMethod;

  static {
    try {
      getFilteredChildrenMethod = ParentRunner.class.getDeclaredMethod("getFilteredChildren");
      getFilteredChildrenMethod.setAccessible(true);
    } catch (NoSuchMethodException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  /**
   * Reflectively invokes a {@link ParentRunner}'s getFilteredChildren method. Manipulating this
   * list lets us control which tests will be run.
   */
  static <T> List<T> getFilteredChildren(ParentRunner<T> parentRunner) {
    try {
      //noinspection unchecked
      return new ArrayList<>((Collection<T>) getFilteredChildrenMethod.invoke(parentRunner));
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException("Failed to invoke getFilteredChildren()", e);
    }
  }

  private ParentRunnerSpy() {
    throw new AssertionError("No instances.");
  }
}
