package com.squareup.burst;

final class Util {
  static <T> T checkNotNull(T o, String message) {
    if (o == null) {
      throw new NullPointerException(message);
    }
    return o;
  }

  private Util() {
    throw new AssertionError("No instances.");
  }
}
