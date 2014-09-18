package com.squareup.burst;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static com.squareup.burst.Util.checkNotNull;

/**
 * Helper methods to facilitate exercising all variations of the declared parameters on test
 * constructors and methods.
 */
public final class Burst {
  private static final Object[][] NONE = new Object[1][0];

  /**
   * Explode a list of argument values for invoking the specified constructor with all combinations
   * of its parameters.
   */
  public static Object[][] explodeArguments(Constructor<?> constructor) {
    checkNotNull(constructor, "constructor");

    return explodeParameters(constructor.getParameterTypes(),
        constructor.getName() + " constructor");
  }

  /**
   * Explode a list of argument values for invoking the specified method with all combinations of
   * its parameters.
   */
  public static Object[][] explodeArguments(Method method) {
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
  public static String friendlyName(Object[] arguments) {
    checkNotNull(arguments, "arguments");
    if (arguments.length == 0) {
      return "";
    }

    StringBuilder builder = new StringBuilder();
    for (Object argument : arguments) {
      if (builder.length() > 0) {
        builder.append(", ");
      }

      Enum<?> value = (Enum<?>) argument;
      // Appends the enum name and value name. (e.g., Card.VISA)
      builder.append(value.getClass().getSimpleName()).append('.').append(value);
    }
    return builder.toString();
  }

  private static Object[][] explodeParameters(Class<?>[] parameterTypes, String name) {
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

  private static Object[][] explode(int count, Enum<?>[][] valuesList) {
    // The number of times to replay iterating over individual enum values.
    int replays = 1;
    // The number of times to repeat an enum value.
    int adjacent = count;

    Object[][] arguments = new Object[count][valuesList.length];
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
