Burst
=====

A unit testing library for varying test data.



Usage
-----

Burst is a set of test runners which rely on enums for varying both the instantiation of test
classes and the methods inside of them.

Define an enum for the property you wish to vary.
```java
public enum Soda {
  PEPSI, COKE
}
```
The enum can be simple as above, or contain data and methods specific to what you are testing.
```java
public enum Sets {
  HASH_SET() {
    @Override public <T> Set<T> create() {
      return new HashSet<T>();
    }
  },
  LINKED_HASH_SET() {
    @Override public <T> Set<T> create() {
      return new LinkedHashSet<T>();
    }
  },
  TREE_SET() {
    @Override public <T> Set<T> create() {
      return new TreeSet<T>();
    }
  }

  public abstract <T> Set<T> create();
}
```

Annotate your test class to use the `BurstJUnit4` runner.
```java
@RunWith(BurstJUnit4.class)
public class DrinkSodaTest {
  // TODO Tests...
}
```

An enum that appears on a test constructor will cause all the enclosed test to be run once for each
value in the enum.
```java
public DrinkSodaTest(Soda soda) {
  // TODO Do something with 'soda'...
}
```

Combine multiple enums for the combination of their variations.
```java
public DrinkSodaTest(Soda soda, Sets sets) {
  // TODO Do something with 'soda' and 'sets'...
}
```
This will be called with the constructor arguments
[`PEPSI` & `HASH_SET`, `PEPSI` & `LINKED_HASH_SET`, `PEPSI` & `TREE_SET`, `COKE` & `HASH_SET`, ..].

Methods may also be varied using one or more parameters that are enums.
```java
@Test public void drinkFavoriteSodas(Soda soda) {
  // TODO Test drink method with 'soda'...
}
```

Having both constructor variation and method variation is supported.
```java
@RunWith(BurstJUnit4.class)
public class DrinkSodaTest {
  private final Set<Soda> favorites;

  public DrinkSodaTest(Sets sets) {
    favorites = sets.create();
  }

  @Test public void trackFavorites() {
    // TODO ...
  }

  @Test public void drinkFavoriteSodas(Soda soda) {
    // TODO ...
  }
}
```
The `trackFavorites` test will be executed 3 times, once for each `Sets` value. The
`drinkFavoriteSodas` test, however, is executed 6 times, for each of the three `Sets` values it
runs twice for each `Soda`.

If a particular variation or variation combination does not make sense you can use [assumptions][1]
to filter either directly in the test or as a custom [rule][2].

### Android

Android currently uses a JUnit 3-based system for testing. Enums are still used for variation but
other aspects need to be changed for support.

In a subclass of `InstrumentationTestRunner`, an instance of `BurstAndroid` should be provided
as the Android test runner.
```java
public final class ExampleTestRunner extends InstrumentationTestRunner {
  private final BurstAndroid testRunner = new BurstAndroid();

  @Override protected AndroidTestRunner getAndroidTestRunner() {
    return testRunner;
  }
}
```

Due to how test classes are instantiated, constructor variation is only supported if a no-argument
public constructor is also specified.
```java
public DrinkSodaTest() {
}

public DrinkSodaTest(Soda soda) {
  // TODO Do something with 'soda'...
}
```

Method variation is not supported.

An alternate filtering mechanism is provided since JUnit 3 lacks [assumptions][1]. Subclasses of
`BurstAndroid` can override the `isClassApplicable` and `isMethodApplicable` methods to determine
whether or not to include a test class or test method, respectively.



Download
--------

 *  **JUnit 4**

    A test runner which can be used for JUnit 4.

    ```
    com.squareup.burst:burst-junit4:1.0.0
    ```

 *  **Android (JUnit 3)**

    A test runner for use by an `InstrumentationTestRunner` or one of its subclasses.

    ```
    com.squareup.burst:burst-android:1.0.0
    ```

 *  **Core library**

    Contains the core logic which creates the combinations of arguments for both constructors and
    method. Usually not useful on its own.

    ```
    com.squareup.burst:burst:1.0.0
    ```



License
-------

    Copyright 2014 Square, Inc.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.




 [1]: http://junit-team.github.io/junit/javadoc/4.10/org/junit/Assume.html
 [2]: http://junit-team.github.io/junit/javadoc/4.10/org/junit/Rule.html
