package com.squareup.burst;

import org.junit.Test;
import org.junit.runner.RunWith;
import com.squareup.burst.annotation.Burst;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(BurstJUnit4.class)
public class MultipleFieldsTest {
  @Burst public Soda soda;
  @Burst public Snack snack;

  @Test public void testMethod() {
    assertThat(soda).isNotNull();
    assertThat(snack).isNotNull();
  }
}
