package com.example.burst;

import com.squareup.burst.BurstAndroid;
import java.lang.reflect.Method;

/**
 * A test runner which ignores classes and methods annotated with {@link Ignore}.
 */
public class IgnoringTestRunner extends BurstAndroid {
  @Override public boolean isClassApplicable(Class<?> cls) {
    return !cls.isAnnotationPresent(Ignore.class);
  }

  @Override public boolean isMethodApplicable(Class<?> cls, Method method) {
    return !method.isAnnotationPresent(Ignore.class);
  }
}
