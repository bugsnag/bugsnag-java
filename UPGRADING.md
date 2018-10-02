Upgrading
=========

## Migrating from bugsnag-java to bugsnag-spring

If you develop a [Spring Framework](https://spring.io/) application, it is recommended that you migrate from bugsnag-java to bugsnag-spring. bugsnag-spring adds support for various Spring-specific features, such as automatic detection of exceptions within scheduled tasks. To upgrade:

1. Replace the `bugsnag` artefact with `bugsnag-spring` in your build.gradle:

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
        return Bugsnag.init("your-api-key-here");
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
