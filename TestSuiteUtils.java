package com.squareup.instrumentation;

import com.squareup.instrumentation.variations.VariationValueProvider;
import com.squareup.instrumentation.variations.Variations;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
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
  public static Test maybeExplodeTest(Class<?> testCaseClass, Method method) {
    if (method.isAnnotationPresent(Variations.class)) {
      Variations variations = method.getAnnotation(Variations.class);
      return explodeTest(testCaseClass, method, variations);
    } else {
      RegisterInstrumentationTestCase test =
          (RegisterInstrumentationTestCase) TestSuite.createTest(testCaseClass, method.getName());
      test.setOriginalName(method.getName());
      return test;
    }
  }

  /**
   * Explodes a test method into multiple methods, each using a different combination of values.
   */
  private static TestSuite explodeTest(Class<?> testClass, Method method, Variations variations) {
    try {
      return tryAddTestCasesWithVariations(testClass, method, variations);
    } catch (Exception e) {
      throw new RuntimeException("Error creating tests.", e);
    }
  }

  private static TestSuite tryAddTestCasesWithVariations(Class<?> testClass, Method method,
      Variations variations) throws Exception {
    Map<Class<?>, Collection<?>> variationResultsByType = new HashMap<>();
    for (Class<? extends VariationValueProvider<?>> valueProviderType : variations.value()) {
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
      Variations variations) throws Exception {
    StringBuilder friendlyNameBuilder = new StringBuilder(method.getName());
    for (Class<?> parameterType : parameterMap.keySet()) {
      Object parameterValue = parameterMap.get(parameterType);
      for (Class<?> variationType : variations.value()) {
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
    parameterMaps.add(new HashMap<Class<?>, Object>());

    for (Class<?> type : possibleValuesByType.keySet()) {
      Collection<?> possibleValues = possibleValuesByType.get(type);

      Collection<Map<Class<?>, ?>> expandedParameterMaps = new ArrayList<>();
      for (Map<Class<?>, ?> parameterMap : parameterMaps) {
        for (Object value : possibleValues) {
          Map<Class<?>, Object> expandedParameterMap = new HashMap<>(parameterMap);
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
