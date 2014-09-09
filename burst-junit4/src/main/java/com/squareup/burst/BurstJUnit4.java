package com.squareup.burst;

import java.util.List;
import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;

public class BurstJUnit4 extends Suite {
  public BurstJUnit4(Class<?> cls) throws InitializationError {
    super(cls, explode(cls));
  }

  static List<Runner> explode(Class<?> cls) {
    throw new UnsupportedOperationException("Not implemented.");
  }
}
