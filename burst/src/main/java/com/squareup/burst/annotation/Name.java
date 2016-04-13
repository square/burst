package com.squareup.burst.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field or parameter name for test output. This can help make test output more
 * readable.
 * <p>
 * The following snippet:
 * <pre>
 *   package com.example.yourpackage
 *
 *   public class MyTests {
 *     enum BooleanCondition { TRUE, FALSE }
 *
 *     {@literal @}Name("IsEnabled") private BooleanCondition isEnabled;
 *     {@literal @}Name("IsFlagged") private BooleanCondition isFlagged;
 *
 *     …
 *
 *     {@literal @}Test
 *     public void testConditions() {
 *       …
 *     }
 *   }
 * </pre>
 * Would produce output like this in the runner:
 * <pre>
 *   testCondition[](com.example.yourpackage.MyTest[IsEnabled.TRUE, IsFlagged.TRUE])
 *   testCondition[](com.example.yourpackage.MyTest[IsEnabled.TRUE, IsFlagged.FALSE])
 *   testCondition[](com.example.yourpackage.MyTest[IsEnabled.FALSE, IsFlagged.TRUE])
 *   testCondition[](com.example.yourpackage.MyTest[IsEnabled.FALSE, IsFlagged.FALSE])
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
public @interface Name {

  /**
   * @return The String value that Burst should use for the name in test output.
   */
  String value();
}
