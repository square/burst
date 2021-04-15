Burst
=====

A unit testing library for varying test data.

**DEPRECATED**: Burst remains stable and functional, but you should check out [TestParameterInjector](https://github.com/google/TestParameterInjector) from Google which is like Burst++. Unless there are major bugs in Burst which necessitate a patch release, no new development will be occurring.



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
  };

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

If your constructor is just setting fields, you can just annotate the fields with `@Burst`.
```java
@RunWith(BurstJUnit4.class)
public class DrinkSodaTest {
  @Burst Soda soda;
  @Burst Sets sets;
  // TODO Tests...
}
```
This behaves just like the above example.

**Note:** Classes can either have constructors with arguments *or* annotated fields. A class with both will cause the test runner to throw an exception.

Methods may also be varied using one or more parameters that are enums.
```java
@Test public void drinkFavoriteSodas(Soda soda) {
  // TODO Test drink method with 'soda'...
}
```

Having both constructor (or field) variation and method variation is supported.
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



Download
--------

 *  **JUnit 4**

    A test runner which can be used for JUnit 4.

    ```
    com.squareup.burst:burst-junit4:1.2.0
    ```

 *  **Core library**

    Contains the core logic which creates the combinations of arguments for both constructors and
    method. Usually not useful on its own.

    ```
    com.squareup.burst:burst:1.2.0
    ```

Snapshots of the development version are available in [Sonatype's `snapshots` repository][snap].



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




 [1]: http://junit.org/javadoc/latest/org/junit/Assume.html
 [2]: http://junit.org/javadoc/latest/org/junit/Rule.html
 [snap]: https://oss.sonatype.org/content/repositories/snapshots/
