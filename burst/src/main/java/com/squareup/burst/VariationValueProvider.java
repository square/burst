package com.squareup.burst;

import java.util.Collection;

public interface VariationValueProvider<T> {

  /** Provide all the values generated for each variation. */
  Collection<T> values();

  /** The type of variation, always {@link Class} of {@link T}. */
  Class<T> type();

  /**
   * Friendly name generated for the given {@link T} value. This is used for generating test names.
   */
  String friendlyName(T value);
}
