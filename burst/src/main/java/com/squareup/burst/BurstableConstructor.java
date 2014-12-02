package com.squareup.burst;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public final class BurstableConstructor {
  /**
   * Find the constructor of cls that is the default constructor if the class has burstable fields,
   * or the non-default constructor if it doesn't.
   * @throws IllegalArgumentException if cls has burstable fields and a non-default constructor
   */
  public static Constructor<?> findSingle(Class<?> cls) {
    final Constructor<?>[] constructors = findAll(cls);

    if (constructors.length == 0) {
      throw new IllegalStateException(cls.getName() + " requires at least 1 public constructor");
    } else if (constructors.length == 1) {
      return constructors[0];
    } else {
      return singleParameterizedConstructorOrThrow(constructors,
          "Class "
              + cls.getName()
              + " has too many constructors. "
              + "Should be 0 or 2 (one no-args, one with enum variations).");
    }
  }

  /**
   * Finds all constructors of cls that are burstable.
   * A burstable constructor is public, and may or may not be the default constructor.
   * @throws IllegalStateException if cls has public fields and a non-default constructor
   */
  private static Constructor<?>[] findAll(Class<?> cls) {
    final Constructor<?>[] constructors = cls.getConstructors();
    final List<Constructor<?>> filteredConstructors = new ArrayList<>();

    for (Constructor<?> constructor : constructors) {
      filteredConstructors.add(constructor);
    }

    return filteredConstructors.toArray(new Constructor<?>[filteredConstructors.size()]);
  }

  /**
   * Finds the constructor that has parameters.
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
