Change Log
==========

Version 1.1.1 *(2016-12-07)*
----------------------------

 * Fix: Pass method annotations when creating JUnit Descriptions.
 * Fix: Do not invoke BeforeClass/AfterClass/ClassRule for each variation.


Version 1.1.0 *(2015-04-15)*
----------------------------

 * New: Support for JUnit 4.12.
 * JUnit 3 support has been dropped. If you are on Android, use the new 'testing-support-lib' for JUnit 4 on-device tests. This also moves to forbidding default constructors in addition to a burst-enabled constructor.


Version 1.0.2 *(2014-12-09)*
----------------------------

 * Added support for field injection with the `@Burst` field annotation.


Version 1.0.1 *(2014-10-21)*
----------------------------

 * Fix: Correct filtering so individual tests can be run from the IDE.


Version 1.0.0 *(2014-10-08)*
----------------------------

Initial release.
