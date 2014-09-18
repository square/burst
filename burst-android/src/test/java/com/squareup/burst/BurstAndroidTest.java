package com.squareup.burst;

import java.lang.reflect.Constructor;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@SuppressWarnings("UnusedDeclaration") // All constructors are inspected via reflection.
public class BurstAndroidTest {
  enum AnEnum {}

  static class SingleNoArg {
    public SingleNoArg() {
    }
  }

  @Test public void singleNoArgConstructor() {
    Constructor<?> constructor = BurstAndroid.findBurstableConstructor(SingleNoArg.class);
    assertThat(constructor).isNotNull();
  }

  static class NoArgAndVariable {
    public NoArgAndVariable() {}
    public NoArgAndVariable(AnEnum anEnum) {}
  }

  @Test public void noArgAndVariableConstructor() {
    Constructor<?> constructor = BurstAndroid.findBurstableConstructor(NoArgAndVariable.class);
    assertThat(constructor).isNotNull();
    assertThat(constructor.getParameterTypes()).containsExactly(AnEnum.class);
  }

  static class MoreThanTwo {
    public MoreThanTwo() {}
    public MoreThanTwo(AnEnum anEnum) {}
    public MoreThanTwo(Object other) {}
  }

  @Test public void moreThanTwoConstructors() {
    try {
      BurstAndroid.findBurstableConstructor(MoreThanTwo.class);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessageContaining("too many constructors");
    }
  }

  static class NoPublic {
    private NoPublic() {}
  }

  @Test(expected = AssertionError.class)
  public void noPublicConstructors() {
    BurstAndroid.findBurstableConstructor(NoPublic.class);
  }
}
