package com.squareup.burst;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/** Reflection on constructors, methods, and their parameters. */
@SuppressWarnings("UnusedDeclaration")
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

  @Rule public final ExpectedException thrown = ExpectedException.none();

  @Test public void nonEnumConstructorParameter() {
    TestConstructor constructor = new TestConstructor(Bad.class.getConstructors()[0]);

    thrown.expect(IllegalStateException.class);
    thrown.expectMessage(Bad.class.getName()
        + " constructor parameter #1 type is not an enum. (java.lang.Object)");

    Burst.explodeArguments(constructor);
  }

  @Test public void singleConstructorParameter() {
    TestConstructor constructor = new TestConstructor(One.class.getConstructors()[0]);
    Object[][] objects = Burst.explodeArguments(constructor);
    assertThat(objects).containsExactly(
        new Object[] { First.APPLE },
        new Object[] { First.BEARD },
        new Object[] { First.COUCH }
    );
  }

  @Test public void multipleConstructorParameters() {
    TestConstructor constructor = new TestConstructor(Three.class.getConstructors()[0]);
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

    thrown.expect(IllegalStateException.class);
    thrown.expectMessage(Example.class.getName()
        + ".example method parameter #1 type is not an enum. (java.lang.Object)");

    Burst.explodeArguments(method);
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
    assertThat(actual).isEqualTo("APPLE");
  }

  @Test public void multipleArguments() {
    String actual = Burst.friendlyName(new Enum<?>[] { First.APPLE, Second.EAGLE, Third.ITALY });
    assertThat(actual).isEqualTo("APPLE, EAGLE, ITALY");
  }

  @Test public void singleArgument_named() {
    String actual = Burst.friendlyName(new Enum<?>[] { First.APPLE }, new Annotation[][] {
        {TestUtil.createName("Fruit")}
    });
    assertThat(actual).isEqualTo("Fruit=APPLE");
  }

  @Test public void multipleArguments_named() {
    String actual = Burst.friendlyName(new Enum<?>[] { First.APPLE, Second.EAGLE, Third.ITALY }, new Annotation[][] {
        {TestUtil.createName("Fruit")},
        {TestUtil.createName("Bird")},
        {TestUtil.createName("Country")}
    });
    assertThat(actual).isEqualTo("Fruit=APPLE, Bird=EAGLE, Country=ITALY");
  }

  @Test public void multipleArguments_multipleAnnotations_named() {
    String actual = Burst.friendlyName(new Enum<?>[] { First.APPLE, Second.EAGLE, Third.ITALY }, new Annotation[][] {
        {TestUtil.createFake(), TestUtil.createName("Fruit")},
        {TestUtil.createFake(), TestUtil.createName("Bird")},
        {TestUtil.createName("Country"), TestUtil.createFake()}
    });
    assertThat(actual).isEqualTo("Fruit=APPLE, Bird=EAGLE, Country=ITALY");
  }

}
