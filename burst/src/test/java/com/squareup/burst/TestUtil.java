package com.squareup.burst;

import com.squareup.burst.annotation.Name;

import java.lang.annotation.Annotation;

final class TestUtil {

  static Name createName(final String name) {
    return new Name() {

      @Override public Class<? extends Annotation> annotationType() {
        return Name.class;
      }

      @Override public String value() {
        return name;
      }
    };
  }

  @interface Fake { }

  static Fake createFake() {
    return new Fake() {

      @Override public Class<? extends Annotation> annotationType() {
        return Fake.class;
      }
    };
  }

  private TestUtil() {
    throw new AssertionError("No instances.");
  }
}
