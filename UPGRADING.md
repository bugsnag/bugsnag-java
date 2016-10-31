Upgrading
=========


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
