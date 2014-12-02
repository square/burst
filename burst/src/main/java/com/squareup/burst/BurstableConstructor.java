package com.squareup.burst;

import java.lang.reflect.Constructor;

final class BurstableConstructor {

  /**
   * Find the parameterized constructor of cls if there is one, or the default constructor if
   * there isn't.
   *
   * @throws IllegalStateException if there are multiple parameterized constructors
   */
  public static Constructor<?> findSingle(Class<?> cls) {
    final Constructor<?>[] constructors = cls.getConstructors();

    if (constructors.length == 0) {
      throw new IllegalStateException(cls.getName() + " requires at least 1 public constructor");
    } else if (constructors.length == 1) {
      return constructors[0];
    } else {
      return singleParameterizedConstructorOrThrow(constructors,
          "Class "
              + cls.getName()
              + " has too many parameterized constructors. "
              + "Should only be 1 (with enum variations).");
    }
  }

  /**
   * Finds the constructor that has parameters.
   * E.g. Given [Ctor(), Ctor(Object)], returns Ctor(Object).
   *
   * @throws IllegalStateException if more than one constructor has parameters
   */
  private static Constructor<?> singleParameterizedConstructorOrThrow(
      final Constructor<?>[] constructors, final String message) {
    Constructor<?> parameterizedConstructor = null;

    for (Constructor<?> constructor : constructors) {
      if (constructor.getParameterTypes().length > 0) {
        if (null == parameterizedConstructor) {
          parameterizedConstructor = constructor;
        } else {
          throw new IllegalStateException(message);
        }
      }
    }

    return parameterizedConstructor;
  }

  private BurstableConstructor() {
    throw new AssertionError("No instances.");
  }
}
