package com.squareup.burst;

import com.squareup.burst.annotation.Burst;
import com.squareup.burst.annotation.Name;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(BurstJUnit4.class)
public class MultipleNamedFieldsTest {
  @Burst @Name("Drink") public Soda soda;
  @Burst public Snack snack;

  @Test public void testMethod() {
    assertThat(soda).isNotNull();
    assertThat(snack).isNotNull();
  }
}
