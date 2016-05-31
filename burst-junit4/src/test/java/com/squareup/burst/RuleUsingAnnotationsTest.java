package com.squareup.burst;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collection;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runner.RunWith;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(BurstJUnit4.class)
public class RuleUsingAnnotationsTest {

  @Target(METHOD)
  @Retention(RUNTIME) @interface CustomAnnotation {

  }

  static class RuleWithAnnotation extends TestWatcher {

    Collection<Annotation> annotations;

    @Override
    protected void starting(Description description) {
      annotations = description.getAnnotations();
    }
  }

  @Rule
  public RuleWithAnnotation rule = new RuleWithAnnotation();

  @Test
  @CustomAnnotation
  public void shouldDetectAnnotationsOnATest() {
    assertEquals(2, rule.annotations.size());

    boolean junitTestAnnotationDetected = false;
    boolean customAnnotationDetected = false;

    for (Annotation annotation : rule.annotations) {
      if (annotation.annotationType() == Test.class) {
        junitTestAnnotationDetected = true;
      } else if (annotation.annotationType() == CustomAnnotation.class) {
        customAnnotationDetected = true;
      }
    }

    assertTrue(junitTestAnnotationDetected);
    assertTrue(customAnnotationDetected);
  }
}
