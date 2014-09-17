package com.squareup.burst;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

import static com.squareup.burst.Util.checkNotNull;
import static java.util.Collections.unmodifiableList;

public final class BurstJUnit4 extends Suite {
  public BurstJUnit4(Class<?> cls) throws InitializationError {
    super(cls, explode(cls));
  }

  static List<Runner> explode(Class<?> cls) throws InitializationError {
    checkNotNull(cls, "cls");

    TestClass testClass = new TestClass(cls);
    List<FrameworkMethod> testMethods = testClass.getAnnotatedMethods(Test.class);

    List<FrameworkMethod> burstMethods = new ArrayList<>(testMethods.size());
    for (FrameworkMethod testMethod : testMethods) {
      Method method = testMethod.getMethod();
      for (Object[] methodArgs : Burst.explodeArguments(method)) {
        burstMethods.add(new BurstMethod(method, methodArgs));
      }
    }

    Constructor<?> constructor = Burst.findConstructor(cls);
    Object[][] constructorArgsList = Burst.explodeArguments(constructor);
    List<Runner> burstRunners = new ArrayList<>(constructorArgsList.length);
    for (Object[] constructorArgs : constructorArgsList) {
      burstRunners.add(new BurstRunner(cls, constructor, constructorArgs, burstMethods));
    }

    return unmodifiableList(burstRunners);
  }

  static String nameWithArguments(String name, Object[] arguments) {
    if (arguments.length == 0) {
      return name;
    }
    return name + '[' + Burst.friendlyName(arguments) + ']';
  }
}
