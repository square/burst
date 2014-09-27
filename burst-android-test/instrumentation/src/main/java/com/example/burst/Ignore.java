package com.example.burst;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicates that a test should always be skipped. Similar to junit 4's @Ignore annotation.
 */
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface Ignore {
}
