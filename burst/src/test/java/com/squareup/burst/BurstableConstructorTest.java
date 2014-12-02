package com.squareup.burst;

import java.lang.reflect.Constructor;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.assertj.core.api.Assertions.assertThat;

public class BurstableConstructorTest {
  enum First {  }
  enum Second {  }

  @Rule public final ExpectedException thrown = ExpectedException.none();

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
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("requires at least 1 public constructor");

    BurstableConstructor.findSingle(NonPublic.class);
  }

  public static class TooMany {
    public TooMany(First first) {}
    public TooMany(Second second) {}
  }
  @Test public void tooMany() {
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage(
        "has too many parameterized constructors. Should only be 1 (with enum variations).");

    BurstableConstructor.findSingle(TooMany.class);
  }

  public static class NonEnumInConstructor {
    public NonEnumInConstructor(Object first) {}
  }
  @Test public void nonEnumInConstructor() {
    Constructor<?> ctor = BurstableConstructor.findSingle(NonEnumInConstructor.class);
    assertThat(ctor.getParameterTypes()).containsExactly(Object.class);
  }
}
