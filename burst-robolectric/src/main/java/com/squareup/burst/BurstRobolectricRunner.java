package com.squareup.burst;

import android.app.Application;
import android.os.Build;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;
import org.robolectric.AndroidManifest;
import org.robolectric.EnvHolder;
import org.robolectric.MavenCentral;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.SdkConfig;
import org.robolectric.SdkEnvironment;
import org.robolectric.TestLifecycle;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.WithConstantInt;
import org.robolectric.annotation.WithConstantString;
import org.robolectric.internal.ParallelUniverse;
import org.robolectric.internal.ParallelUniverseInterface;
import org.robolectric.res.ResourceLoader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.squareup.burst.BurstJUnit4.nameWithArguments;
import static com.squareup.burst.Util.checkNotNull;
import static org.fest.reflect.core.Reflection.constructor;
import static org.fest.reflect.core.Reflection.staticField;
import static org.fest.reflect.core.Reflection.type;


class BurstRobolectricRunner extends RobolectricTestRunner {
  private final Enum<?>[] constructorArgs;
  private final List<FrameworkMethod> methods;

  @SuppressWarnings("UnusedParameters")
  BurstRobolectricRunner(Class<?> cls, Constructor<?> ignored, Enum<?>[] constructorArgs,
              List<FrameworkMethod> methods) throws InitializationError {
    super(checkNotNull(cls, "cls"));
    this.constructorArgs = checkNotNull(constructorArgs, "constructorArgs");
    this.methods = checkNotNull(methods, "methods");

    EnvHolder envHolder;
    synchronized (envHoldersByTestRunner) {
      Class<? extends BurstRobolectricRunner> testRunnerClass = getClass();
      envHolder = envHoldersByTestRunner.get(testRunnerClass);
      if (envHolder == null) {
        envHolder = new EnvHolder();
        envHoldersByTestRunner.put(testRunnerClass, envHolder);
      }
    }
    this.envHolder = envHolder;
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

  @Override protected void validateConstructor(List<Throwable> errors) {
    // Constructor was already validated by Burst.
  }

  @Override protected void validatePublicVoidNoArgMethods(Class<? extends Annotation> annotation,
      boolean isStatic, List<Throwable> errors) {
    // Methods were already validated by Burst.
  }

  private static final MavenCentral MAVEN_CENTRAL = new MavenCentral();
  private static final Map<Class<? extends BurstRobolectricRunner>, EnvHolder>
      envHoldersByTestRunner = new HashMap<>();
  private final EnvHolder envHolder;
  private TestLifecycle<Application> testLifecycle;

  static {
    // This starts up the Poller SunPKCS11-Darwin thread early,
    // outside of any Robolectric classloader.
    new SecureRandom();
  }

  private Class<? extends BurstRobolectricRunner> lastTestRunnerClass;
  private SdkConfig lastSdkConfig;
  private SdkEnvironment lastSdkEnvironment;
  private final HashSet<Class<?>> loadedTestClasses = new HashSet<>();

  private void assureTestLifecycle(SdkEnvironment sdkEnvironment) {
    try {
      ClassLoader robolectricClassLoader = sdkEnvironment.getRobolectricClassLoader();
      testLifecycle = (TestLifecycle) robolectricClassLoader.loadClass(
          getTestLifecycleClass().getName()).newInstance();
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected Statement classBlock(RunNotifier notifier) {
    final Statement statement = childrenInvoker(notifier);
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        try {
          statement.evaluate();
          for (Class<?> testClass : loadedTestClasses) {
            invokeAfterClass(testClass);
          }
        } finally {
          afterClass();
        }
      }
    };
  }

  private void invokeAfterClass(final Class<?> clazz) throws Throwable {
    final TestClass testClass = new TestClass(clazz);
    final List<FrameworkMethod> afters = testClass.getAnnotatedMethods(AfterClass.class);
    for (FrameworkMethod after : afters) {
      after.invokeExplosively(null);
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

        Class bootstrappedTestClass = sdkEnvironment
            .bootstrappedClass(getTestClass().getJavaClass());
        BurstHelperTestRunner helperTestRunner = getHelperTestRunner(bootstrappedTestClass);

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
          Class<? extends Enum> enumClass =
              (Class<? extends Enum>) bootstrappedMethod.getParameterTypes()[i];
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

          ResourceLoader systemResourceLoader = sdkEnvironment.getSystemResourceLoader(
              MAVEN_CENTRAL, BurstRobolectricRunner.this);
          setUpApplicationState(bootstrappedMethod, parallelUniverseInterface, strictI18n,
              systemResourceLoader, appManifest, config);
          testLifecycle.beforeTest(bootstrappedMethod);
        } catch (Exception e) {
          e.printStackTrace();
          throw new RuntimeException(e);
        }

        final Statement statement = helperTestRunner.methodBlock(
            new BurstMethod(bootstrappedMethod, args));

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
              // Afterward too, so stuff doesn't hold on to classes?
              parallelUniverseInterface.resetStaticState();
              // TODO: Is this really needed?
              Thread.currentThread()
                  .setContextClassLoader(RobolectricTestRunner.class.getClassLoader());
            }
          }
        }
      }
    };
  }

  private void invokeBeforeClass(final Class clazz) throws Throwable {
    if (!loadedTestClasses.contains(clazz)) {
      loadedTestClasses.add(clazz);

      final TestClass testClass = new TestClass(clazz);
      final List<FrameworkMethod> befores = testClass.getAnnotatedMethods(BeforeClass.class);
      for (FrameworkMethod before : befores) {
        before.invokeExplosively(null);
      }
    }
  }

  @Override
  protected BurstHelperTestRunner getHelperTestRunner(Class bootstrappedTestClass) {
    try {
      return new BurstHelperTestRunner(bootstrappedTestClass);
    } catch (InitializationError initializationError) {
      throw new RuntimeException(initializationError);
    }
  }

  private SdkEnvironment getEnvironment(final AndroidManifest appManifest, final Config config) {
    final SdkConfig sdkConfig = pickSdkVersion(appManifest, config);

    // Keep the most recently-used SdkEnvironment strongly reachable
    // to prevent thrashing in low-memory situations.
    if (getClass().equals(lastTestRunnerClass) && sdkConfig.equals(lastSdkConfig)) {
      return lastSdkEnvironment;
    }

    lastTestRunnerClass = null;
    lastSdkConfig = null;
    lastSdkEnvironment = envHolder.getSdkEnvironment(sdkConfig, new SdkEnvironment.Factory() {
      @Override public SdkEnvironment create() {
        return createSdkEnvironment(sdkConfig);
      }
    });
    lastTestRunnerClass = getClass();
    lastSdkConfig = sdkConfig;
    return lastSdkEnvironment;
  }

  @Override
  protected void setUpApplicationState(Method method, ParallelUniverseInterface
      parallelUniverseInterface, boolean strictI18n, ResourceLoader systemResourceLoader,
                                       AndroidManifest appManifest, Config config) {
    parallelUniverseInterface.setUpApplicationState(method, testLifecycle, strictI18n,
        systemResourceLoader, appManifest, config);
  }

  private ParallelUniverseInterface getHooksInterface(SdkEnvironment sdkEnvironment) {
    ClassLoader robolectricClassLoader = sdkEnvironment.getRobolectricClassLoader();
    Class<? extends ParallelUniverseInterface> parallelUniverseClass =
        type(ParallelUniverse.class.getName())
            .withClassLoader(robolectricClassLoader)
            .loadAs(ParallelUniverseInterface.class);

    return constructor()
        .withParameterTypes(RobolectricTestRunner.class)
        .in(parallelUniverseClass)
        .newInstance(this);
  }

  @Override
  public void internalAfterTest(final Method method) {
    testLifecycle.afterTest(method);
  }

  private void afterClass() {
    testLifecycle = null;
  }

  /**
   * Find all the class and method annotations and pass them to
   * addConstantFromAnnotation() for evaluation.
   * <p/>
   * TODO: Add compound annotations to support defining more than one int and string at a time
   * http://stackoverflow.com/questions/1554112/multiple-annotations-of-the-same-type-on-one-element
   *
   * @param method
   * @return
   */
  private Map<Field, Object> getWithConstantAnnotations(Method method) {
    Map<Field, Object> constants = new HashMap<>();

    for (Annotation anno : method.getDeclaringClass().getAnnotations()) {
      addConstantFromAnnotation(constants, anno);
    }

    for (Annotation anno : method.getAnnotations()) {
      addConstantFromAnnotation(constants, anno);
    }

    return constants;
  }


  /**
   * If the annotation is a constant redefinition, add it to the provided hash
   *
   * @param constants
   * @param anno
   */
  private void addConstantFromAnnotation(Map<Field, Object> constants, Annotation anno) {
    try {
      String name = anno.annotationType().getName();
      Object newValue;

      if (name.equals(WithConstantString.class.getName())) {
        newValue = anno.annotationType().getMethod("newValue").invoke(anno);
      } else if (name.equals(WithConstantInt.class.getName())) {
        newValue = anno.annotationType().getMethod("newValue").invoke(anno);
      } else {
        return;
      }

      @SuppressWarnings("rawtypes")
      Class classWithField = (Class) anno.annotationType().getMethod("classWithField").invoke(anno);
      String fieldName = (String) anno.annotationType().getMethod("fieldName").invoke(anno);
      Field field = classWithField.getDeclaredField(fieldName);
      constants.put(field, newValue);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Defines static finals from the provided hash and stores the old values back
   * into the hash.
   * <p/>
   * Call it twice with the same hash, and it puts everything back the way it was originally.
   *
   * @param constants
   */
  private void setupConstants(Map<Field, Object> constants) {
    for (Field field : constants.keySet()) {
      Object newValue = constants.get(field);
      Object oldValue = Robolectric.Reflection.setFinalStaticField(field, newValue);
      constants.put(field, oldValue);
    }
  }

  public class BurstHelperTestRunner extends HelperTestRunner {
    public BurstHelperTestRunner(Class<?> testClass) throws InitializationError {
      super(testClass);
    }

    @Override public Statement classBlock(RunNotifier notifier) {
      return super.classBlock(notifier);
    }

    @Override public Statement methodBlock(FrameworkMethod method) {
      return super.methodBlock(method);
    }

    @Override protected void validateConstructor(List<Throwable> errors) {
      BurstRobolectricRunner.this.validateConstructor(errors);
    }

    @Override
    protected void validatePublicVoidNoArgMethods(Class<? extends Annotation> annotation,
        boolean isStatic, List<Throwable> errors) {
      BurstRobolectricRunner.this.validatePublicVoidNoArgMethods(annotation, isStatic, errors);
    }

    @Override
    protected Object createTest() throws Exception {
      Constructor<?> parallelConstructor = null;
      Constructor<?>[] constructors = getTestClass().getJavaClass().getConstructors();
      for (Constructor<?> aConstructor : constructors) {
        if (aConstructor.getParameterTypes().length == constructorArgs.length) {
          parallelConstructor = aConstructor;
        }
      }
      if (parallelConstructor == null) {
        throw new IllegalStateException("Got lost in the parallel universe");
      }
      Enum[] parallelArgs = new Enum[parallelConstructor.getParameterTypes().length];
      for (int i = 0; i < constructorArgs.length; i++) {
        Class<? extends Enum> enumClass =
            (Class<? extends Enum>) parallelConstructor.getParameterTypes()[i];
        String enumName = constructorArgs[i].name();
        parallelArgs[i] = Enum.valueOf(enumClass, enumName);
      }
      Object test = parallelConstructor.newInstance(parallelArgs);
      testLifecycle.prepareTest(test);
      return test;
    }
  }
}
