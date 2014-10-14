package org.robolectric.annotation;

/**
 * Indicates that a JUnit test class or method should be checked for I18N/L10N-safety.
 *
 * @deprecated Use <a href="http://tools.android.com/tips/lint">Android Lint</a> instead.
 * @see DisableStrictI18n
 */
@java.lang.annotation.Documented
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({java.lang.annotation.ElementType.TYPE,java.lang.annotation.ElementType.METHOD})
public @interface EnableStrictI18n {
}
