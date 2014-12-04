package com.squareup.burst;

import com.squareup.burst.annotation.Burst;
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
    TestConstructor ctor = BurstableConstructor.findSingle(None.class);
    assertThat(ctor.getVariationTypes()).isEmpty();
  }

  public static class Default {
    public Default() {}
  }
  @Test public void defaultConstructor() {
    TestConstructor ctor = BurstableConstructor.findSingle(Default.class);
    assertThat(ctor.getVariationTypes()).isEmpty();
  }

  public static class One {
    public One(First first) {}
  }
  @Test public void singleParameterizedConstructor() {
    TestConstructor ctor = BurstableConstructor.findSingle(One.class);
    assertThat(ctor.getVariationTypes()).containsExactly(First.class);
  }

  public static class DefaultAndOne {
    public DefaultAndOne() {}
    public DefaultAndOne(First first) {}
  }
  @Test public void defaultAndParameterizedConstructor() {
    TestConstructor ctor = BurstableConstructor.findSingle(DefaultAndOne.class);
    assertThat(ctor.getVariationTypes()).containsExactly(First.class);
  }

  public static class NonPublicConstructor {
    NonPublicConstructor() {}
  }
  @Test public void nonPublic() {
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("requires at least 1 public constructor");

    BurstableConstructor.findSingle(NonPublicConstructor.class);
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
    TestConstructor ctor = BurstableConstructor.findSingle(NonEnumInConstructor.class);
    assertThat(ctor.getVariationTypes()).containsExactly(Object.class);
  }

  public static class NotAnEnumInConstructor {
    public NotAnEnumInConstructor(Object first) {}
  }
  @Test public void notAnEnumInConstructor() {
    TestConstructor ctor = BurstableConstructor.findSingle(NotAnEnumInConstructor.class);
    assertThat(ctor.getVariationTypes()).containsExactly(Object.class);
  }

  public static class DefaultWithField {
    @Burst First first;
  }
  @Test public void defaultConstructorWithField() {
    TestConstructor ctor = BurstableConstructor.findSingle(DefaultWithField.class);
    assertThat(ctor.getVariationTypes()).containsExactly(First.class);
  }

  public static class NoneWithField {
    @Burst First first;
    public NoneWithField() {}
  }
  @Test public void singleEmptyConstructorWithField() {
    TestConstructor ctor = BurstableConstructor.findSingle(NoneWithField.class);
    assertThat(ctor.getVariationTypes()).containsExactly(First.class);
  }

  public static class MultipleFields {
    @Burst First first;
    @Burst Second second;
  }
  @Test public void emptyConstructorMultipleFields() {
    TestConstructor ctor = BurstableConstructor.findSingle(MultipleFields.class);
    assertThat(ctor.getVariationTypes()).containsOnly(First.class, Second.class);
  }

  public static class OneWithField {
    @Burst Second second;
    public OneWithField(First first) {}
  }
  @Test public void singleParameterizedConstructorWithField() {
    thrown.expectMessage(
        "has a parameterized constructor, so cannot also be parameterized on fields");

    BurstableConstructor.findSingle(OneWithField.class);
  }

  public static class PrivateField {
    @Burst private First first;
  }
  @Test public void privateAnnotatedField() {
    TestConstructor ctor = BurstableConstructor.findSingle(PrivateField.class);
    assertThat(ctor.getVariationTypes()).containsExactly(First.class);
  }

  public static class InheritedField extends PrivateField {
    @Burst Second second;
  }
  @Test public void inheritedField() {
    TestConstructor ctor = BurstableConstructor.findSingle(InheritedField.class);
    assertThat(ctor.getVariationTypes()).containsOnly(First.class, Second.class);
  }

  public static class StaticField {
    @Burst static First first;
  }
  @Test public void StaticField() {
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("Burstable field must not be static");

    BurstableConstructor.findSingle(StaticField.class);
  }

  public static class FinalField {
    @Burst final First first = null;
  }
  @Test public void finalField() {
    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("Burstable field must not be final");

    BurstableConstructor.findSingle(FinalField.class);
  }

  public static class NotAnEnumField {
    @Burst Object first;
  }
  @Test public void notAnEnumField() {
    TestConstructor ctor = BurstableConstructor.findSingle(NotAnEnumField.class);
    assertThat(ctor.getVariationTypes()).containsExactly(Object.class);
  }

  public static class UnannotatedField {
    First first;
    @Burst Second second;
  }
  @Test public void unannotatedField() {
    TestConstructor ctor = BurstableConstructor.findSingle(UnannotatedField.class);
    assertThat(ctor.getVariationTypes()).containsExactly(Second.class);
  }
}
