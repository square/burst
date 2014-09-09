// Copyright 2014 Square, Inc.
package com.squareup.burst;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME) @Target(METHOD)
public @interface Variations {

  Class<? extends VariationValueProvider<?>>[] value();
}
