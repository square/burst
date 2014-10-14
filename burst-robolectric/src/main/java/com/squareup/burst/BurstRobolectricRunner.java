package com.squareup.burst;

import android.os.Build;
import org.junit.runner.Description;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.robolectric.AndroidManifest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.SdkEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.internal.ParallelUniverseInterface;
import org.robolectric.res.ResourceLoader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static com.squareup.burst.BurstJUnit4.nameWithArguments;
import static com.squareup.burst.Util.checkNotNull;
import static org.fest.reflect.core.Reflection.staticField;

public class BurstRobolectricRunner extends RobolectricTestRunner {
  private final Constructor<?> constructor;
  private final Enum<?>[] constructorArgs;
  private final List<FrameworkMethod> methods;

  public BurstRobolectricRunner(Class<?> cls, Constructor<?> constructor, Enum<?>[] constructorArgs,
                                List<FrameworkMethod> methods) throws InitializationError {
    super(checkNotNull(cls, "cls"));
    this.constructor = checkNotNull(constructor, "constructor");
    this.constructorArgs = checkNotNull(constructorArgs, "constructorArgs");
    this.methods = checkNotNull(methods, "methods");
  }

  @Override protected void validateConstructor(List<Throwable> errors) {
    // Constructor was already validated by Burst.
  }

  @Override
  protected void validatePublicVoidNoArgMethods(Class<? extends Annotation> annotation,
                                                boolean isStatic, List<Throwable> errors) {
    // Methods were already validated by Burst.
  }

  @Override
  protected List<FrameworkMethod> getChildren() {
    return methods;
  }

  @Override
  protected String getName() {
    return nameWithArguments(super.getName(), constructorArgs);
  }

  @Override
  protected Description describeChild(FrameworkMethod method) {
    return Description.createTestDescription(getName(), method.getName());
  }


  @Override
  public Object createTest() throws Exception {
    return constructor.newInstance(constructorArgs);
  }

  @Override
  protected HelperTestRunner getHelperTestRunner(final Class bootstrappedTestClass) {
    try {
      return new HelperTestRunner(bootstrappedTestClass) {
        @Override protected void validateConstructor(List<Throwable> errors) {
          BurstRobolectricRunner.this.validateConstructor(errors);
        }

        @Override
        protected void validatePublicVoidNoArgMethods(Class<? extends Annotation> annotation, boolean isStatic,
            List<Throwable> errors) {
          BurstRobolectricRunner.this.validatePublicVoidNoArgMethods(annotation, isStatic, errors);
        }

        @Override
        protected Object createTest() throws Exception {
          Constructor<?> parallelConstructor = bootstrappedTestClass.getConstructors()[0];
          Enum[] parallelArgs = new Enum[parallelConstructor.getParameterTypes().length];
          for (int i = 0; i < constructorArgs.length; i++) {
            Class<? extends Enum> enumClass = (Class<? extends Enum>) parallelConstructor.getParameterTypes()[i];
            String enumName = constructorArgs[i].name();
            parallelArgs[i] = Enum.valueOf(enumClass, enumName);
          }

          return parallelConstructor.newInstance(parallelArgs);
        }
      };
    } catch (InitializationError initializationError) {
      throw new RuntimeException(initializationError);
    }
  }

  @Override protected Statement methodBlock(final FrameworkMethod method) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        BurstMethod burstMethod = (BurstMethod) method;
        final Config config = getConfig(method.getMethod());
        AndroidManifest appManifest = getAppManifest(config);
        SdkEnvironment sdkEnvironment = getEnvironment(appManifest, config);
        Thread.currentThread().setContextClassLoader(sdkEnvironment.getRobolectricClassLoader());

        Class bootstrappedTestClass = sdkEnvironment.bootstrappedClass(getTestClass().getJavaClass());
        HelperTestRunner helperTestRunner = getHelperTestRunner(bootstrappedTestClass);

        Method bootstrappedMethod = null;
        Method[] declaredMethods = bootstrappedTestClass.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
          if (declaredMethod.getName().equals(method.getMethod().getName()) &&
              burstMethod.getMethodArgs().length == declaredMethod.getParameterTypes().length) {
            bootstrappedMethod = declaredMethod;
          }
        }
        if (bootstrappedMethod == null) {
          throw new NoSuchMethodException(method.getMethod().getName());
        }
        Enum[] args = new Enum[burstMethod.getMethodArgs().length];
        for (int i = 0; i < burstMethod.getMethodArgs().length; i++) {
          Class<? extends Enum> enumClass = (Class<? extends Enum>) bootstrappedMethod.getParameterTypes()[i];
          String enumName = burstMethod.getMethodArgs()[i].name();
          args[i] = Enum.valueOf(enumClass, enumName);
        };

        configureShadows(sdkEnvironment, config);

        ParallelUniverseInterface parallelUniverseInterface = getHooksInterface(sdkEnvironment);
        try {
          // Only invoke @BeforeClass once per class
          if (!loadedTestClasses.contains(bootstrappedTestClass)) {
            invokeBeforeClass(bootstrappedTestClass);
          }
          assureTestLifecycle(sdkEnvironment);

          parallelUniverseInterface.resetStaticState();
          parallelUniverseInterface.setSdkConfig(sdkEnvironment.getSdkConfig());

          boolean strictI18n = determineI18nStrictState(bootstrappedMethod);

          int sdkVersion = pickReportedSdkVersion(config, appManifest);
          Class<?> versionClass = sdkEnvironment.bootstrappedClass(Build.VERSION.class);
          staticField("SDK_INT").ofType(int.class).in(versionClass).set(sdkVersion);

          ResourceLoader systemResourceLoader = sdkEnvironment.getSystemResourceLoader(MAVEN_CENTRAL, BurstRobolectricRunner.this);
          setUpApplicationState(bootstrappedMethod, parallelUniverseInterface, strictI18n, systemResourceLoader, appManifest, config);
          testLifecycle.beforeTest(bootstrappedMethod);
        } catch (Exception e) {
          e.printStackTrace();
          throw new RuntimeException(e);
        }

        final Statement statement = helperTestRunner.methodBlock(new BurstMethod(bootstrappedMethod, args));

        Map<Field, Object> withConstantAnnos = getWithConstantAnnotations(bootstrappedMethod);

        // todo: this try/finally probably isn't right -- should mimic RunAfters? [xw]
        try {
          if (withConstantAnnos.isEmpty()) {
            statement.evaluate();
          } else {
            synchronized (this) {
              setupConstants(withConstantAnnos);
              statement.evaluate();
              setupConstants(withConstantAnnos);
            }
          }
        } finally {
          try {
            parallelUniverseInterface.tearDownApplication();
          } finally {
            try {
              internalAfterTest(bootstrappedMethod);
            } finally {
              parallelUniverseInterface.resetStaticState(); // afterward too, so stuff doesn't hold on to classes?
              // todo: is this really needed?
              Thread.currentThread().setContextClassLoader(RobolectricTestRunner.class.getClassLoader());
            }
          }
        }
      }
    };
  }
}