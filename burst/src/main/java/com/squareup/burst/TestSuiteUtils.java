package com.squareup.burst;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Utilities for creating test suites for Register testing.
 */
public class TestSuiteUtils {

  private TestSuiteUtils() {
    // No instances.
  }

  /**
   * Explodes a given test method if it is annotated with @Variations, in which case it will be
   * replaced by multiple methods, each using a different combination of values.
   */
  public static Test explodeTest(Class<?> testCaseClass, Method method) {
    List<Class<? extends VariationValueProvider<?>>> variationsList = Collections.emptyList();

    Variations variations = method.getAnnotation(Variations.class);
    if (variations != null) {
      variationsList = Arrays.asList(variations.value());
    }

    try {
      return tryExplodeTest(testCaseClass, method, variationsList);
    } catch (Exception e) {
      throw new RuntimeException("Error creating tests.", e);
    }
  }

  private static TestSuite tryExplodeTest(Class<?> testClass, Method method,
      List<Class<? extends VariationValueProvider<?>>> variations)
      throws Exception {
    Map<Class<?>, Collection<?>> variationResultsByType = new LinkedHashMap<>();
    for (Class<? extends VariationValueProvider<?>> valueProviderType : variations) {
      VariationValueProvider<?> valueProvider = valueProviderType.newInstance();
      variationResultsByType.put(valueProvider.type(), valueProvider.values());
    }

    Constructor constructor = findMatchingConstructor(testClass, variationResultsByType.keySet());
    Class[] constructorTypes = constructor.getParameterTypes();

    // For each combination of values, generate a test with those values and add it to the suite.
    TestSuite result = new TestSuite();
    for (Map<Class<?>, ?> parameterMap : getParameterMaps(variationResultsByType)) {
      Object[] constructorArgs = new Object[constructorTypes.length];
      for (int i = 0; i < constructorArgs.length; i++) {
        constructorArgs[i] = parameterMap.get(constructorTypes[i]);
      }

      RegisterInstrumentationTestCase testCase =
          (RegisterInstrumentationTestCase) constructor.newInstance(constructorArgs);
      testCase.setOriginalName(method.getName());
      testCase.setName(getFriendlyTestName(method, parameterMap, variations));
      result.addTest(testCase);
    }
    return result;
  }

  /**
   * Generates a "friendly" method name which includes info about which values were used for types
   * with variations.
   */
  private static String getFriendlyTestName(Method method, Map<Class<?>, ?> parameterMap,
      List<Class<? extends VariationValueProvider<?>>> variations)
      throws Exception {
    StringBuilder friendlyNameBuilder = new StringBuilder(method.getName());
    for (Class<?> parameterType : parameterMap.keySet()) {
      Object parameterValue = parameterMap.get(parameterType);
      for (Class<?> variationType : variations) {
        @SuppressWarnings("unchecked") VariationValueProvider<Object> valueProvider =
            (VariationValueProvider<Object>) variationType.newInstance();
        if (valueProvider.type().equals(parameterType)) {
          friendlyNameBuilder.append('_').append(valueProvider.friendlyName(parameterValue));
          break;
        }
      }
    }
    return friendlyNameBuilder.toString();
  }

  /**
   * Given the possible values for each type, compute all possible combinations of values.
   */
  private static Collection<Map<Class<?>, ?>> getParameterMaps(
      Map<Class<?>, Collection<?>> possibleValuesByType) {
    Collection<Map<Class<?>, ?>> parameterMaps = new ArrayList<>();
    parameterMaps.add(new LinkedHashMap<Class<?>, Object>());

    for (Class<?> type : possibleValuesByType.keySet()) {
      Collection<?> possibleValues = possibleValuesByType.get(type);

      Collection<Map<Class<?>, ?>> expandedParameterMaps = new ArrayList<>();
      for (Map<Class<?>, ?> parameterMap : parameterMaps) {
        for (Object value : possibleValues) {
          Map<Class<?>, Object> expandedParameterMap = new LinkedHashMap<>(parameterMap);
          expandedParameterMap.put(type, value);
          expandedParameterMaps.add(expandedParameterMap);
        }
      }
      parameterMaps = expandedParameterMaps;
    }

    return parameterMaps;
  }

  /**
   * Find a constructor which accepts a superset of all the required parameters.
   */
  private static Constructor<?> findMatchingConstructor(Class<?> clazz, Set<Class<?>> paramTypes) {
    for (Constructor<?> constructor : clazz.getConstructors()) {
      if (Arrays.asList(constructor.getParameterTypes()).containsAll(paramTypes)) {
        return constructor;
      }
    }
    throw new IllegalArgumentException("No constructor matches " + paramTypes);
  }
}
