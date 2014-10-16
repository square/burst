package com.squareup.burst;

import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;

import static com.squareup.burst.BurstJUnit4.explode;

public class BurstRobolectric extends Suite {
  public BurstRobolectric(Class<?> cls) throws InitializationError {
    super(cls, explode(cls, BurstRobolectricRunner.class));
  }
}
