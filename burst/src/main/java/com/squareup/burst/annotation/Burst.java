package com.squareup.burst.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field to be injected by Burst when unit tests are run.
 * If a test class has fields annotated with this, it must have a default constructor,
 * and no other constructors.
 * <p>
 * The following are equivalent:
 * <pre>
 *   public class MyTests {
 *     enum Color { RED, BLUE }
 *
 *     private final Color color;
 *
 *     public MyTests(Color color) {
 *       this.color = color;
 *     }
 *
 *     …
 *   }
 * </pre>
 * <pre>
 *   public class MyTests {
 *     enum Color { RED, BLUE }
 *
 *     {@literal @}Burst private Color color;
 *
 *     …
 *   }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Burst {
}
