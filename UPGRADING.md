Upgrading
=========

## 3.x to 4.x

*This major release modernises the Java notifier with breaking changes to align with current Bugsnag conventions, drop legacy Java/Servlet support, and add new capabilities like feature flags.*

### Java 17+ required

The minimum Java version has been raised from Java 7 to **Java 17**. You must compile and run on Java 17 or above.

### Javax servlet support removed

Support for `javax.servlet` has been removed. Only **Jakarta Servlet API 5.0+** is now supported. If your application still uses `javax.servlet`, you will need to migrate to Jakarta before upgrading. The `bugsnag-spring:javax` submodule has been removed entirely.

### Report renamed to BugsnagEvent

The `Report` class has been renamed to `BugsnagEvent`. Update any references in callbacks and direct usage:

```java
// Before
Report report = bugsnag.buildReport(exception);
report.setSeverity(Severity.ERROR);

// After
// BugsnagEvent is now provided directly via callbacks (buildReport has been removed)
bugsnag.notify(exception, (event) -> {
    event.setSeverity(Severity.ERROR);
    return true;
});
```

### Callback renamed to OnErrorCallback

The `Callback` interface has been replaced by `OnErrorCallback`. The method signature has also changed — `onError` now returns a `boolean` to control whether the event is delivered:

```java
// Before
bugsnag.addCallback((report) -> {
    report.setSeverity(Severity.ERROR);
});

// After
bugsnag.addOnError((event) -> {
    event.setSeverity(Severity.ERROR);
    return true; // return false to suppress delivery
});
```

The `BugsnagMarker` constructor for Logback integration now also takes `OnErrorCallback` instead of `Callback`.

### addCallback renamed to addOnError

The method for registering global callbacks has been renamed:

```java
// Before
bugsnag.addCallback(callback);

// After
bugsnag.addOnError(callback);
```

### Metadata method changes

The `addToTab` method on events has been renamed to `addMetadata`:

```java
// Before
report.addToTab("tab", "key", "value");

// After
event.addMetadata("tab", "key", "value");
```

The static thread metadata methods have also been renamed (note the lowercase 'd'):

```java
// Before
Bugsnag.addThreadMetaData("tab", "key", "value");
Bugsnag.clearThreadMetaData();

// After
Bugsnag.addThreadMetadata("tab", "key", "value");
Bugsnag.clearThreadMetadata();
```

### setFilters renamed to setRedactedKeys

The method for specifying keys whose values should be redacted from metadata has been renamed:

```java
// Before
bugsnag.setFilters("password", "secret");

// After
bugsnag.setRedactedKeys("password", "secret");
```

### setIgnoreClasses renamed to setDiscardClasses

The method for ignoring exceptions by class name now takes `Pattern` objects instead of plain strings, giving you full regex control:

```java
// Before
bugsnag.setIgnoreClasses("com.example.IgnoreMe");

// After
bugsnag.setDiscardClasses(Pattern.compile("com\\.example\\.IgnoreMe"));
```

### setNotifyReleaseStages renamed to setEnabledReleaseStages

```java
// Before
bugsnag.setNotifyReleaseStages("production", "staging");

// After
bugsnag.setEnabledReleaseStages("production", "staging");
```

### setSendThreads now takes ThreadSendPolicy

The boolean `setSendThreads` has been replaced with a `ThreadSendPolicy` enum for finer control:

```java
// Before
bugsnag.setSendThreads(true);  // always send
bugsnag.setSendThreads(false); // never send

// After
bugsnag.setSendThreads(ThreadSendPolicy.ALWAYS);
bugsnag.setSendThreads(ThreadSendPolicy.UNHANDLED_ONLY); // new option
bugsnag.setSendThreads(ThreadSendPolicy.NEVER);
```

### Endpoint configuration

A new `EndpointConfiguration` class has been introduced for configuring custom endpoints. The previous string-based `setEndpoints(String, String)` method is now deprecated in favour of:

```java
// Before
bugsnag.setEndpoints("https://notify.example.com", "https://sessions.example.com");

// After
bugsnag.setEndpoints(new EndpointConfiguration(
    "https://notify.example.com",
    "https://sessions.example.com"
));
```

### buildReport removed

The `buildReport(Throwable)` method has been removed from `Bugsnag`. Use the callback-based `notify` methods instead to modify events before delivery.

### Feature flags (new)

A new feature flags API has been added for annotating events with experiment and A/B test information. Feature flags can be set globally on the client or per-event:

```java
// Add flags globally
bugsnag.addFeatureFlag("checkout-v2", "enabled");
bugsnag.addFeatureFlag("dark-mode");

// Add/remove flags per-event in a callback
bugsnag.addOnError((event) -> {
    event.addFeatureFlag("experiment-123", "variant-a");
    event.clearFeatureFlag("old-flag");
    return true;
});

// Clear all flags
bugsnag.clearFeatureFlags();
```

### New public model classes

Several internal classes are now part of the public API, providing richer access to error data in callbacks:

- **`BugsnagError`** — access `getErrorClass()`, `getMessage()`, and `getStacktrace()` on the underlying error
- **`BugsnagThread`** — access `getId()`, `getName()`, `getStacktrace()`, and `isErrorReportingThread()` on captured threads
- **`Stackframe`** — access `getFile()`, `getMethod()`, `getLineNumber()`, and `isInProject()` on individual stack frames
- **`FeatureFlag`** — created via `FeatureFlag.of("name")` or `FeatureFlag.of("name", "variant")`

### Spring integration changes

- The `bugsnag-spring:javax` submodule has been removed. Only Jakarta-based Spring (Spring 6 / Spring Boot 3+) is supported.
- Spring configuration classes now use `OnErrorCallback` rather than `Callback`.
- If you were importing `JavaxMvcConfiguration` or `SpringBootJavaxConfiguration`, switch to `JakartaMvcConfiguration` and `SpringBootJakartaConfiguration` respectively.

### Logback configuration changes

Logback XML configuration properties have been updated to match the new API names:

| Old property | New property |
|---|---|
| `<filteredProperties>` | `<redactedKeys>` |
| `<ignoredClasses>` | `<discardClasses>` |
| `<notifyReleaseStages>` | `<enabledReleaseStages>` |
| `<sendThreads>` (boolean) | `<sendThreads>` (enum: `ALWAYS`, `UNHANDLED_ONLY`, `NEVER`) |

New properties are also available: `<featureFlags>` for declaring feature flags in your Logback configuration.

### Serializer is now an interface

`Serializer` has been changed from a concrete class to an interface. If you implemented custom delivery, update to use `DefaultSerializer` as the concrete implementation.

## Migrating from bugsnag-java to bugsnag-spring

If you develop a [Spring Framework](https://spring.io/) application, it is recommended that you migrate from bugsnag-java to bugsnag-spring. bugsnag-spring adds support for various Spring-specific features, such as automatic detection of exceptions within scheduled tasks. To upgrade:

1. Replace the `bugsnag` artifact with `bugsnag-spring` in your build.gradle:

    ```groovy
    //compile 'com.bugsnag:bugsnag:3.+'
    compile 'com.bugsnag:bugsnag-spring:3.+'
    ```

2. Create a Spring `Configuration` class which exposes `Bugsnag` as a Spring bean and imports the configuration class `BugsnagSpringConfiguration`. This should replace any previous instantiation of `Bugsnag`:

    ```java
    @Configuration
    @Import(BugsnagSpringConfiguration.class)
    public class BugsnagConfig {
        @Bean
        public Bugsnag bugsnag() {
            return new Bugsnag("your-api-key-here");
        }
    }
    ```

3. If you wish to configure Logback, capture uncaught exceptions in async methods, or otherwise customise your integration, please [see the docs](https://docs.bugsnag.com/platforms/java/spring/#installation) for further information.

## 2.x to 3.x

*The Java notifier library has gone through some major improvements, and there are some small changes you'll need to make to upgrade.*

#### Java 1.6+

Java 1.6 and above is now required.

#### Class name changes

The main `Client` class has been renamed to `Bugsnag`. The `Event` class (used in callbacks) has been renamed to `Report`.

#### Severity now an enum

`Severity` is now an enum instead of a `String`.

#### Java 8 Lambda syntax

Java 8 Lambda syntax is now supported in callbacks.  For example:

```
bugsnag.addCallback((report) -> {
    report.setSeverity(Severity.ERROR);
});
```

or from a bugsnag.notify() call:
```
bugsnag.notify(ex, (report) -> {
    report.addToTab("tab", "key", "value");
});
```

#### Chaining support

Chaining support added to `Report` methods:

```
bugsnag.addCallback((report) -> {
    report.setSeverity(Severity.ERROR).setUserId("123");
});
```

#### Custom endpoint requires protocol

Setting a custom endpoint now requires the protocol to be set:

```
bugsnag.setEndpoint("https://bugsnag.internal.example:49000");
```
