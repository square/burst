package com.squareup.burst;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

@SuppressWarnings("UnusedDeclaration") // Reflection on constructors, methods, and their parameters.
public class BurstTest {
  enum First { APPLE, BEARD, COUCH }
  enum Second { DINGO, EAGLE }
  enum Third { FRANK, GREAT, HEAVY, ITALY }

  public static class One {
    public One(First first) {}
  }
  public static class Three {
    public Three(First first, Second second, Third third) {}
  }

  public static class Bad {
    public Bad(Object o) {}
  }

  @Test public void nonEnumConstructorParameter() {
    Constructor<?> constructor = Bad.class.getConstructors()[0];
    try {
      Burst.explodeArguments(constructor);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessage(Bad.class.getName()
          + " constructor parameter #1 type is not an enum. (java.lang.Object)");
    }
  }

  @Test public void singleConstructorParameter() {
    Constructor<?> constructor = One.class.getConstructors()[0];
    Object[][] objects = Burst.explodeArguments(constructor);
    assertThat(objects).containsExactly(
        new Object[] { First.APPLE },
        new Object[] { First.BEARD },
        new Object[] { First.COUCH }
    );
  }

  @Test public void multipleConstructorParameters() {
    Constructor<?> constructor = Three.class.getConstructors()[0];
    Object[][] objects = Burst.explodeArguments(constructor);
    assertThat(objects).containsExactly(
        new Object[] { First.APPLE, Second.DINGO, Third.FRANK },
        new Object[] { First.APPLE, Second.DINGO, Third.GREAT },
        new Object[] { First.APPLE, Second.DINGO, Third.HEAVY },
        new Object[] { First.APPLE, Second.DINGO, Third.ITALY },
        new Object[] { First.APPLE, Second.EAGLE, Third.FRANK },
        new Object[] { First.APPLE, Second.EAGLE, Third.GREAT },
        new Object[] { First.APPLE, Second.EAGLE, Third.HEAVY },
        new Object[] { First.APPLE, Second.EAGLE, Third.ITALY },
        new Object[] { First.BEARD, Second.DINGO, Third.FRANK },
        new Object[] { First.BEARD, Second.DINGO, Third.GREAT },
        new Object[] { First.BEARD, Second.DINGO, Third.HEAVY },
        new Object[] { First.BEARD, Second.DINGO, Third.ITALY },
        new Object[] { First.BEARD, Second.EAGLE, Third.FRANK },
        new Object[] { First.BEARD, Second.EAGLE, Third.GREAT },
        new Object[] { First.BEARD, Second.EAGLE, Third.HEAVY },
        new Object[] { First.BEARD, Second.EAGLE, Third.ITALY },
        new Object[] { First.COUCH, Second.DINGO, Third.FRANK },
        new Object[] { First.COUCH, Second.DINGO, Third.GREAT },
        new Object[] { First.COUCH, Second.DINGO, Third.HEAVY },
        new Object[] { First.COUCH, Second.DINGO, Third.ITALY },
        new Object[] { First.COUCH, Second.EAGLE, Third.FRANK },
        new Object[] { First.COUCH, Second.EAGLE, Third.GREAT },
        new Object[] { First.COUCH, Second.EAGLE, Third.HEAVY },
        new Object[] { First.COUCH, Second.EAGLE, Third.ITALY }
    );
  }

  @Test public void noMethodParameters() throws NoSuchMethodException {
    class Example {
      public void example() {}
    }
    Method method = Example.class.getMethod("example");

    Object[][] objects = Burst.explodeArguments(method);
    assertThat(objects).containsExactly(new Object[0]);
  }

  @Test public void nonEnumMethodParameter() throws NoSuchMethodException {
    class Example {
      public void example(Object o) {}
    }
    Method method = Example.class.getMethod("example", Object.class);

    try {
      Burst.explodeArguments(method);
      fail();
    } catch (IllegalStateException e) {
      assertThat(e).hasMessage(Example.class.getName()
          + ".example method parameter #1 type is not an enum. (java.lang.Object)");
    }
  }

  @Test public void singleMethodParameters() throws NoSuchMethodException {
    class Example {
      public void example(First first) {}
    }
    Method method = Example.class.getMethod("example", First.class);

    Object[][] objects = Burst.explodeArguments(method);
    assertThat(objects).containsExactly(
        new Object[] { First.APPLE },
        new Object[] { First.BEARD },
        new Object[] { First.COUCH }
    );
  }

  @Test public void multipleMethodParameters() throws NoSuchMethodException {
    class Example {
      public void example(First first, Second second, Third third) {}
    }
    Method method = Example.class.getMethod("example", First.class, Second.class, Third.class);

    Object[][] objects = Burst.explodeArguments(method);
    assertThat(objects).containsExactly(
        new Object[] { First.APPLE, Second.DINGO, Third.FRANK },
        new Object[] { First.APPLE, Second.DINGO, Third.GREAT },
        new Object[] { First.APPLE, Second.DINGO, Third.HEAVY },
        new Object[] { First.APPLE, Second.DINGO, Third.ITALY },
        new Object[] { First.APPLE, Second.EAGLE, Third.FRANK },
        new Object[] { First.APPLE, Second.EAGLE, Third.GREAT },
        new Object[] { First.APPLE, Second.EAGLE, Third.HEAVY },
        new Object[] { First.APPLE, Second.EAGLE, Third.ITALY },
        new Object[] { First.BEARD, Second.DINGO, Third.FRANK },
        new Object[] { First.BEARD, Second.DINGO, Third.GREAT },
        new Object[] { First.BEARD, Second.DINGO, Third.HEAVY },
        new Object[] { First.BEARD, Second.DINGO, Third.ITALY },
        new Object[] { First.BEARD, Second.EAGLE, Third.FRANK },
        new Object[] { First.BEARD, Second.EAGLE, Third.GREAT },
        new Object[] { First.BEARD, Second.EAGLE, Third.HEAVY },
        new Object[] { First.BEARD, Second.EAGLE, Third.ITALY },
        new Object[] { First.COUCH, Second.DINGO, Third.FRANK },
        new Object[] { First.COUCH, Second.DINGO, Third.GREAT },
        new Object[] { First.COUCH, Second.DINGO, Third.HEAVY },
        new Object[] { First.COUCH, Second.DINGO, Third.ITALY },
        new Object[] { First.COUCH, Second.EAGLE, Third.FRANK },
        new Object[] { First.COUCH, Second.EAGLE, Third.GREAT },
        new Object[] { First.COUCH, Second.EAGLE, Third.HEAVY },
        new Object[] { First.COUCH, Second.EAGLE, Third.ITALY }
    );
  }

  @Test public void noArguments() {
    String actual = Burst.friendlyName(new Enum<?>[0]);
    assertThat(actual).isEqualTo("");
  }

  @Test public void singleArgument() {
    String actual = Burst.friendlyName(new Enum<?>[] { First.APPLE });
    assertThat(actual).isEqualTo("First.APPLE");
  }

  @Test public void multipleArguments() {
    String actual = Burst.friendlyName(new Enum<?>[] { First.APPLE, Second.EAGLE, Third.ITALY });
    assertThat(actual).isEqualTo("First.APPLE, Second.EAGLE, Third.ITALY");
  }
}
