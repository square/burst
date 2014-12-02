package com.squareup.burst;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import static com.squareup.burst.Util.checkNotNull;

/**
 * A wrapper around {@link Constructor} that can also set fields reflectively.
 */
final class TestConstructor {
  private final Constructor<?> constructor;
  private final Field[] fields;

  public TestConstructor(Constructor<?> constructor, Field... fields) {
    this.constructor = checkNotNull(constructor, "constructor");
    this.fields = fields;

    for (Field field : fields) {
      field.setAccessible(true);
    }
  }

  public String getName() {
    return constructor.getName();
  }

  /**
   * Returns an array of the Class objects associated with the fields this constructor was
   * initialized with, followed by the parameter types of this constructor.
   * If the constructor was declared with no fields and no parameters, an empty array will be
   * returned.
   */
  public Class<?>[] getVariationTypes() {
    final Class<?>[] ctorTypes = constructor.getParameterTypes();
    final Class<?>[] allTypes = new Class<?>[ctorTypes.length + fields.length];

    System.arraycopy(ctorTypes, 0, allTypes, 0, ctorTypes.length);

    for (int i = 0; i < fields.length; i++) {
      allTypes[i + ctorTypes.length] = fields[i].getType();
    }

    return allTypes;
  }

  /**
   * Calls {@link Constructor#newInstance(java.lang.Object...)}, as
   * well as initializing all the fields passed to
   * {@link TestConstructor#TestConstructor(Constructor, Field...)}.
   * <p>
   * Constructor arguments should be first in the array, followed by field arguments.
   */
  public Object newInstance(Object[] args)
      throws IllegalAccessException, InvocationTargetException, InstantiationException {
    // Partition arg list
    final int ctorArgCount = constructor.getParameterTypes().length;
    final Object[] ctorArgs = Arrays.copyOfRange(args, 0, ctorArgCount);
    final Object[] fieldArgs = Arrays.copyOfRange(args, ctorArgCount, args.length);

    return newInstance(ctorArgs, fieldArgs);
  }

  private Object newInstance(Object[] ctorArgs, Object[] fieldArgs)
      throws IllegalAccessException, InvocationTargetException, InstantiationException {
    final Object instance = constructor.newInstance(ctorArgs);
    initializeFieldsOnInstance(instance, fieldArgs);
    return instance;
  }

  private void initializeFieldsOnInstance(final Object instance, final Object[] args)
      throws IllegalAccessException {
    if (fields.length != args.length) {
      throw new IllegalArgumentException(String.format(
          "Requires values for %d fields, only %d values passed", fields.length, args.length));
    }

    for (int i = 0; i < fields.length; i++) {
      fields[i].set(instance, args[i]);
    }
  }
}
