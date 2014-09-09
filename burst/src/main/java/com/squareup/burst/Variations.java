// Copyright 2014 Square, Inc.
package com.squareup.burst;

import java.lang.annotation.Retention;
import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME) @Qualifier
public @interface Variations {

  Class<? extends VariationValueProvider<?>>[] value();
}
