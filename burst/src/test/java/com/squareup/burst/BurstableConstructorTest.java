package com.squareup.burst;

import java.lang.reflect.Constructor;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

public class BurstableConstructorTest {
  enum First {  }
  enum Second {  }

  public static class None {
  }
  @Test public void noConstructor() {
    Constructor<?> ctor = BurstableConstructor.findSingle(None.class);
    assertThat(ctor).isNotNull();
    assertThat(ctor.getParameterTypes()).isEmpty();
  }

  public static class Default {
    public Default() {}
  }
  @Test public void defaultConstructor() {
    Constructor<?> ctor = BurstableConstructor.findSingle(Default.class);
    assertThat(ctor).isNotNull();
    assertThat(ctor.getParameterTypes()).isEmpty();
  }

  public static class One {
    public One(First first) {}
  }
  @Test public void singleParameterizedConstructor() {
    Constructor<?> ctor = BurstableConstructor.findSingle(One.class);
    assertThat(ctor).isNotNull();
    assertThat(ctor.getParameterTypes()).containsExactly(First.class);
  }

  public static class DefaultAndOne {
    public DefaultAndOne() {}
    public DefaultAndOne(First first) {}
  }
  @Test public void defaultAndParameterizedConstructor() {
    Constructor<?> ctor = BurstableConstructor.findSingle(DefaultAndOne.class);
    assertThat(ctor).isNotNull();
    assertThat(ctor.getParameterTypes()).containsExactly(First.class);
  }

  public static class NonPublic {
    NonPublic() {}
  }
  @Test public void nonPublic() {
    try {
      Constructor<?> ctor = BurstableConstructor.findSingle(NonPublic.class);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessage(NonPublic.class.getName() + " requires at least 1 public constructor");
    }
  }

  public static class TooMany {
    public TooMany(First first) {}
    public TooMany(Second second) {}
  }
  @Test public void tooMany() {
    try {
      Constructor<?> ctor = BurstableConstructor.findSingle(TooMany.class);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessage(
          "Class "
              + TooMany.class.getName()
              + " has too many constructors. Should be 0 or 2 (one no-args, one with enum variations).");
    }
  }
}
