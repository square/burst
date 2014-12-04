package com.squareup.burst;

import com.squareup.burst.annotation.Burst;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

final class BurstableConstructor {

  /**
   * Find the parameterized constructor of cls if there is one, or the default constructor if
   * there isn't.
   *
   * @throws IllegalStateException if there are multiple parameterized constructors
   */
  public static TestConstructor findSingle(Class<?> cls) {
    final TestConstructor[] constructors = findAll(cls);

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
   * Finds all constructors of {@code cls} that are burstable.
   * A burstable constructor is public, and may or may not be the default constructor.
   *
   * @throws IllegalStateException if cls has public fields and a non-default constructor
   */
  private static TestConstructor[] findAll(Class<?> cls) {
    final Constructor<?>[] constructors = cls.getConstructors();
    final Field[] fields = getBurstableFields(cls);
    final List<TestConstructor> filteredConstructors = new ArrayList<>();

    for (Constructor<?> constructor : constructors) {
      if (constructor.getParameterTypes().length > 0 && fields.length > 0) {
        throw new IllegalStateException(
            "Class "
            + cls.getName()
            + " has a parameterized constructor, so cannot also be parameterized on fields");
      }

      filteredConstructors.add(new TestConstructor(constructor, fields));
    }

    return filteredConstructors.toArray(new TestConstructor[filteredConstructors.size()]);
  }

  /**
   * Finds the constructor that has parameters.
   * Assumes at least one non-default constructor is present.
   * E.g. Given [Ctor(), Ctor(Object)], returns Ctor(Object).
   *
   * @throws IllegalStateException if more than one constructor has parameters
   */
  private static TestConstructor singleParameterizedConstructorOrThrow(
      final TestConstructor[] constructors, final String message) {
    TestConstructor parameterizedConstructor = null;

    for (TestConstructor constructor : constructors) {
      if (constructor.getVariationTypes().length > 0) {
        if (null == parameterizedConstructor) {
          parameterizedConstructor = constructor;
        } else {
          throw new IllegalStateException(message);
        }
      }
    }

    if (null == parameterizedConstructor) {
      throw new IllegalArgumentException("Must pass at least one parameterized constructor");
    }

    return parameterizedConstructor;
  }

  /**
   * Get all the {@link Burst}-annotated fields.
   * Validates that the field is non-static and non-final, but doesn't check type (types are
   * validated by static methods in {@link com.squareup.burst.Burst}).
   */
  private static Field[] getBurstableFields(Class<?> cls) {
    final List<Field> fields = new ArrayList<>();

    for (Field field : getAllFields(cls)) {
      if (field.isAnnotationPresent(Burst.class)) {

        if (Modifier.isStatic(field.getModifiers())) {
          throw new IllegalStateException("Burstable field must not be static: " + field.getName());
        }

        if (Modifier.isFinal(field.getModifiers())) {
          throw new IllegalStateException("Burstable field must not be final: " + field.getName());
        }

        fields.add(field);
      }
    }

    return fields.toArray(new Field[fields.size()]);
  }

  /**
   * Returns all fields of any visibility on type and all of type's superclasses.
   * Aka {@link Class#getFields()}, but returns all fields, not just public ones.
   */
  private static Collection<Field> getAllFields(Class<?> type) {
    final Collection<Field> allFields = new ArrayList<>();

    while (type != Object.class) {
      Collections.addAll(allFields, type.getDeclaredFields());
      type = type.getSuperclass();
    }

    return Collections.unmodifiableCollection(allFields);
  }

  private BurstableConstructor() {
    throw new AssertionError("No instances.");
  }
}
