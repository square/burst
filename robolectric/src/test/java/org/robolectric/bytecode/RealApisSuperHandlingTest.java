package org.robolectric.bytecode;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.TestRunners;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.internal.Instrument;
import org.robolectric.annotation.RealObject;

import static org.junit.Assert.assertEquals;
import static org.robolectric.Robolectric.directlyOn;

@RunWith(TestRunners.RealApisWithoutDefaults.class)
public class RealApisSuperHandlingTest {
  @Ignore("requires that superclass shadow methods can be called directly from shadowed class")
  @Test
  @Config(shadows = {ChildShadow.class, ParentShadow.class, GrandparentShadow.class})
  public void subclassesNotExplicitlyMarkedInstrumentedShouldBeAbleToCallSuperWithoutLooping() throws Exception {
    assertEquals("4-3s-3-2s-2-1s-1-boof", new BabiesHavingBabies().method("boof"));
    /*
     * Something like:
     *   directlyOn(realObject, Parent.class).method("boof") to call Parent's boof()
     */
  }

  @Ignore("requires that superclass shadow methods can be called directly from shadowed class")
  @Test
  @Config(shadows = {ChildShadow.class, ParentShadow.class, GrandparentShadow.class})
  public void shadowInvocationWhenAllAreShadowed() throws Exception {
    assertEquals("3s-3-2s-2-1s-1-boof", new Child().method("boof"));
    assertEquals("2s-2-1s-1-boof", new Parent().method("boof"));
    assertEquals("1s-1-boof", new Grandparent().method("boof"));
  }

  @Ignore("requires that superclass shadow methods can be called directly from shadowed class")
  @Test
  @Config(shadows = {ParentShadow.class, GrandparentShadow.class})
  public void shadowInvocationWhenChildIsInstrumentedButUnshadowed() throws Exception {
    assertEquals("2s-2-1s-1-boof", new Child().method("boof")); // unfortunate arrangement, ok to change
    assertEquals("2s-2-1s-1-boof", new Parent().method("boof"));
    assertEquals("1s-1-boof", new Grandparent().method("boof"));
  }

  @Ignore("requires that superclass shadow methods can be called directly from shadowed class")
  @Test
  @Config(shadows = {ParentShadow.class})
  public void whenIntermediateIsShadowed() throws Exception {
    assertEquals("2s-3-2-1-boof", new Child().method("boof")); // unfortunate arrangement, ok to change
    assertEquals("2s-2-1-boof", new Parent().method("boof"));
    assertEquals("1-boof", new Grandparent().method("boof"));
  }

  @Ignore // todo we need to figure out a better way to deal with this...
  @Test public void whenNoneAreShadowed() throws Exception {
    assertEquals("3-2-1-boof", new Child().method("boof"));
    assertEquals("2-1-boof", new Parent().method("boof"));
    assertEquals("1-boof", new Grandparent().method("boof"));
  }

  @Implements(Child.class)
  public static class ChildShadow extends ParentShadow {
    private @RealObject Child realObject;

    @Override @Implementation
    public String method(String value) {
      return "3s-" + directlyOn(realObject, Child.class).method(value);
    }
  }

  @Implements(Parent.class)
  public static class ParentShadow extends GrandparentShadow {
    private @RealObject Parent realObject;

    @Override @Implementation public String method(String value) {
      return "2s-" + directlyOn(realObject, Parent.class).method(value);
    }
  }

  @Implements(Grandparent.class)
  public static class GrandparentShadow {
    private @RealObject Grandparent realObject;

    @SuppressWarnings("UnusedDeclaration")
    private void __constructor__() {
    } // todo we need to figure out a better way to deal with this...

    @Implementation public String method(String value) {
      return "1s-" + directlyOn(realObject, Grandparent.class).method(value);
    }
  }

  private static class BabiesHavingBabies extends Child {
    @Override
    public String method(String value) {
      return "4-" + super.method(value);
    }
  }

  @Instrument
  public static class Child extends Parent {
    @Override public String method(String value) {
      return "3-" + super.method(value);
    }
  }

  @Instrument
  public static class Parent extends Grandparent {
    @Override public String method(String value) {
      return "2-" + super.method(value);
    }
  }

  @Instrument
  public static class Grandparent {
    public String method(String value) {
      return "1-" + value;
    }
  }
}
