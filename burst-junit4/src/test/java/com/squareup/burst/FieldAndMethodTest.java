package com.squareup.burst;

import org.junit.Test;
import org.junit.runner.RunWith;
import com.squareup.burst.annotation.Burst;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(BurstJUnit4.class)
public class FieldAndMethodTest {
  @Burst public Snack snack;

  @Test public void testMethod(Soda soda) {
    assertThat(snack).isNotNull();
    assertThat(soda).isNotNull();
  }
}
