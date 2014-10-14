package com.squareup.burst;

import org.junit.Test;
import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;
import org.robolectric.RobolectricTestRunner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.squareup.burst.BurstJUnit4.explode;
import static com.squareup.burst.Util.checkNotNull;
import static java.util.Collections.unmodifiableList;

public class BurstRobolectric extends Suite {
  public BurstRobolectric(Class<?> cls) throws InitializationError {
    super(cls, explode(cls, BurstRobolectricRunner.class));
  }
}
