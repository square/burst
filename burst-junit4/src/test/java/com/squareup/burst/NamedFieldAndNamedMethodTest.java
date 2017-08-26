package com.squareup.burst;

import com.squareup.burst.annotation.Burst;
import com.squareup.burst.annotation.Name;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(BurstJUnit4.class)
public class NamedFieldAndNamedMethodTest {
  @Burst @Name("Food") public Snack snack;

  @Test public void testMethod(@Name("Drink") Soda soda) {
    assertThat(snack).isNotNull();
    assertThat(soda).isNotNull();
  }
}
