package com.squareup.burst;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.List;
import org.junit.runner.Description;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import static com.squareup.burst.BurstJUnit4.nameWithArguments;
import static com.squareup.burst.Util.checkNotNull;

final class BurstRunner extends BlockJUnit4ClassRunner {
  private final Constructor<?> constructor;
  private final Enum<?>[] constructorArgs;
  private final List<FrameworkMethod> methods;

  BurstRunner(Class<?> cls, Constructor<?> constructor, Enum<?>[] constructorArgs,
      List<FrameworkMethod> methods) throws InitializationError {
    super(checkNotNull(cls, "cls"));
    this.constructor = checkNotNull(constructor, "constructor");
    this.constructorArgs = checkNotNull(constructorArgs, "constructorArgs");
    this.methods = checkNotNull(methods, "methods");
  }

  @Override protected List<FrameworkMethod> getChildren() {
    return methods;
  }

  @Override protected String getName() {
    return nameWithArguments(super.getName(), constructorArgs);
  }

  @Override protected Description describeChild(FrameworkMethod method) {
    return Description.createTestDescription(getName(), method.getName());
  }

  @Override protected Object createTest() throws Exception {
    return constructor.newInstance(constructorArgs);
  }

  @Override protected void validateConstructor(List<Throwable> errors) {
    // Constructor was already validated by Burst.
  }

  @Override protected void validatePublicVoidNoArgMethods(Class<? extends Annotation> annotation,
      boolean isStatic, List<Throwable> errors) {
    // Methods were already validated by Burst.
  }
}
