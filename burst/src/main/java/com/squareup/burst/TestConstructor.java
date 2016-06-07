package com.squareup.burst;

import java.lang.annotation.Annotation;
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

  public Class<?>[] getConstructorParameterTypes() {
    return constructor.getParameterTypes();
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
    if (args.length != getVariationTypes().length) {
      throw new IllegalArgumentException(String.format(
          "Constructor takes %d values, only %d passed", getVariationTypes().length, args.length));
    }

    // Partition arg list
    final Object[] ctorArgs = extractConstructorArgs(args);
    final Object[] fieldArgs = extractFieldArgs(args);

    final Object instance = newInstanceWithoutFields(ctorArgs);
    initializeFieldsOnInstance(instance, fieldArgs);
    return instance;
  }

  /**
   * @return array containing the elements of <code>args</code> that apply to the underlying
   * constructor.
   */
  public Object[] extractConstructorArgs(final Object[] args) {
    return Arrays.copyOfRange(args, 0, constructor.getParameterTypes().length);
  }

  /**
   * @return array containing the elements of <code>args</code> that apply to fields.
   */
  public Object[] extractFieldArgs(final Object[] args) {
    return Arrays.copyOfRange(args, constructor.getParameterTypes().length, args.length);
  }

  /**
   * Creates a new instance by calling the underlying constructor, without initializing
   * any fields.
   */
  public Object newInstanceWithoutFields(Object[] args)
      throws IllegalAccessException, InvocationTargetException, InstantiationException {
    return constructor.newInstance(args);
  }

  /**
   * Sets the fields on an instance (as passed to
   * {@link TestConstructor#TestConstructor(Constructor, Field...)}) to <code>args</code>.
   * The number of arguments in the array must be equal to the number of fields.
   */
  public void initializeFieldsOnInstance(final Object instance, final Object[] args)
      throws IllegalAccessException {
    if (fields.length != args.length) {
      throw new IllegalArgumentException(String.format(
          "Requires values for %d fields, only %d values passed", fields.length, args.length));
    }

    for (int i = 0; i < fields.length; i++) {
      fields[i].set(instance, args[i]);
    }
  }

  public Annotation[][] getArgumentAnnotations() {
    final Annotation[][] ctorAnnotations = constructor.getParameterAnnotations();
    final Annotation[][] allAnnotations = new Annotation[ctorAnnotations.length + fields.length][];

    System.arraycopy(ctorAnnotations, 0, allAnnotations, 0, ctorAnnotations.length);

    for (int i = 0; i < fields.length; i++) {
      Annotation[] sourceAnnotations = fields[i].getAnnotations();
      Annotation[] copiedAnnotations = new Annotation[sourceAnnotations.length];
      System.arraycopy(sourceAnnotations, 0, copiedAnnotations, 0, sourceAnnotations.length);
      allAnnotations[i + ctorAnnotations.length] = copiedAnnotations;
    }

    return allAnnotations;
  }
}
