package com.squareup.burst;

import org.junit.Test;
import org.junit.runner.RunWith;
import com.squareup.burst.annotation.Burst;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(BurstJUnit4.class)
public class SingleFieldTest {
  @Burst public Soda soda;

  @Test public void testMethod() {
    assertThat(soda).isNotNull();
  }
}
