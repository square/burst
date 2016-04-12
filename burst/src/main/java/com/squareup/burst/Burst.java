package com.squareup.burst;

import com.squareup.burst.annotation.Name;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static com.squareup.burst.Util.checkNotNull;

/**
 * Helper methods to facilitate exercising all variations of the declared parameters on test
 * constructors and methods.
 */
public final class Burst {
  private static final Enum<?>[][] NONE = new Enum<?>[1][0];

  /**
   * Explode a list of argument values for invoking the specified constructor with all combinations
   * of its parameters.
   */
  public static Enum<?>[][] explodeArguments(TestConstructor constructor) {
    checkNotNull(constructor, "constructor");

    return explodeParameters(constructor.getVariationTypes(),
        constructor.getName() + " constructor");
  }

  /**
   * Explode a list of argument values for invoking the specified method with all combinations of
   * its parameters.
   */
  public static Enum<?>[][] explodeArguments(Method method) {
    checkNotNull(method, "method");

    return explodeParameters(method.getParameterTypes(),
        method.getDeclaringClass().getName() + '.' + method.getName() + " method");
  }

  /**
   * Creates an "exploded" test name which includes information about the {@code constructorArgs}
   * and {@code methodArgs}. This will append both the enum class and enum value name for every
   * argument in order.
   * <p>
   * For example, a method named "snackBreak" being invoked with constructor arguments
   * {@code Drink.SODA} and {@code Snack.ALMONDS} and method arguments {@code BreakTime.AFTERNOON}
   * would produce "snackBreak_DrinkSODA_SnackALMONDS_BreakTimeAFTERNOON".
   *
   * @throws ClassCastException If any element of {@code constructorArgs} or {@code methodArgs} is
   * not an enum value.
   */
  public static String friendlyName(Enum<?>[] arguments) {
    return friendlyName(arguments, null);
  }

  /**
   * Creates an "exploded" test name which includes information about the {@code constructorArgs}
   * and {@code methodArgs}. This will append both the enum class and enum value name for every
   * argument in order.
   * <p>
   * For example, a method named "snackBreak" being invoked with constructor arguments
   * {@code Drink.SODA} and {@code Snack.ALMONDS} and method arguments {@code BreakTime.AFTERNOON}
   * would produce "snackBreak_DrinkSODA_SnackALMONDS_BreakTimeAFTERNOON".
   * <p>
   * If any of the arguments have an {@literal @}Name annotation, the enum class name will be
   * replaced with the value provided in the annotation.
   *
   * @throws ClassCastException If any element of {@code constructorArgs} or {@code methodArgs} is
   * not an enum value.
   */
  public static String friendlyName(Enum<?>[] arguments, Annotation[][] argumentAnnotations) {
    checkNotNull(arguments, "arguments");
    if (arguments.length == 0) {
      return "";
    }

    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < arguments.length; i++) {
      Object argument = arguments[i];
      if (builder.length() > 0) {
        builder.append(", ");
      }

      Enum<?> value = (Enum<?>) argument;
      // Appends the enum name and value name. (e.g., Card.VISA)
      String name = value.getClass().getSimpleName();
      if (argumentAnnotations != null
          && argumentAnnotations.length > i
          && argumentAnnotations[i] != null) {
        for (Annotation annotation : argumentAnnotations[i]) {
          if (annotation instanceof Name) {
            name = ((Name) annotation).value();
            break;
          }
        }
      }
      builder.append(name).append('.').append(value);
    }
    return builder.toString();
  }

  private static Enum<?>[][] explodeParameters(Class<?>[] parameterTypes, String name) {
    int parameterCount = parameterTypes.length;
    if (parameterCount == 0) {
      return NONE;
    }

    int count = 1; // Total variation count.
    Enum<?>[][] valuesList = new Enum<?>[parameterCount][];

    for (int i = 0; i < parameterCount; i++) {
      Class<?> parameterType = parameterTypes[i];
      if (!parameterType.isEnum()) {
        throw new IllegalStateException(name
            + " parameter #"
            + (i + 1)
            + " type is not an enum. ("
            + parameterType.getName()
            + ')');
      }
      //noinspection unchecked
      Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>) parameterType;

      Enum<?>[] values = enumType.getEnumConstants();
      valuesList[i] = values;
      count *= values.length;
    }
    return explode(count, valuesList);
  }

  private static Enum<?>[][] explode(int count, Enum<?>[][] valuesList) {
    // The number of times to replay iterating over individual enum values.
    int replays = 1;
    // The number of times to repeat an enum value.
    int adjacent = count;

    Enum<?>[][] arguments = new Enum<?>[count][valuesList.length];
    for (int valuesIndex = 0; valuesIndex < valuesList.length; valuesIndex++) {
      Enum<?>[] values = valuesList[valuesIndex];

      // Gap between replays is the previous number of adjacent values.
      int replayGap = adjacent;
      // The number of adjacent is divided among the number of current values.
      adjacent /= values.length;

      for (int replay = 0; replay < replays; replay++) {
        int replayOffset = replay * replayGap;
        for (int valueIndex = 0; valueIndex < values.length; valueIndex++) {
          int valueOffset = valueIndex * adjacent;
          for (int i = 0; i < adjacent; i++) {
            arguments[replayOffset + valueOffset + i][valuesIndex] = values[valueIndex];
          }
        }
      }

      // Increase the iteration replays by the number of values in the current enum.
      replays *= values.length;
    }

    return arguments;
  }

  private Burst() {
    throw new AssertionError("No instances.");
  }
}
