# Changelog

## 3.1.0 (2017-90-22)

### Enhancements

* Track difference between handled and unhandled exceptions
  [#59](https://github.com/bugsnag/bugsnag-java/pull/59)

## 3.0.2 (04 Jan 2017)

### Bug Fixes

* Use deprecated method to ensure we don't break with older versions of jackson
  [William Starling](https://github.com/foygl)
  [#54](https://github.com/bugsnag/bugsnag-java/pull/54)

## 3.0.1 (11 Nov 2016)

### Bug Fixes

* Stop AsyncHttpDelivery from indefinitely blocking exit
  [William Starling](https://github.com/foygl)
  [#50](https://github.com/bugsnag/bugsnag-java/pull/50)

### Enhancements

* Improve Spring compatibility
  [William Starling](https://github.com/foygl)
  [#49](https://github.com/bugsnag/bugsnag-java/pull/49)

## 3.0.0 (31 Oct 2016)

Major rewrite/update of the notifier. Changes include:

* Uses [Jackson](https://github.com/FasterXML/jackson-databind) for streaming JSON serialization
* Logs internally using `org.slf4j.Logger`
* `Severity` is now an enum instead of a `String`
* Request information is automatically collected in Servlet API apps
* JVM runtime, O/S, and locale diagnostics are collected
* The `Client` object has been renamed to `Bugsnag`
* The `Event` object has been renamed to `Report`
* `Report` object is now exposed for ease of attaching diagnostics to error reports
* Targets Java 1.6
* Callbacks now support Java 8 lambda syntax 
* Chaining support added to `Report` methods
* Can now add a Callback to `Bugsnag.notify` calls
* Can now change the API key on a per-report basis using Callbacks
* Error report delivery is now fully swappable via the `Delivery` interface

See [UPGRADING](UPGRADING.md) for upgrade details.

## 2.0.0 (25 Mar 2016)

This is a major release for the release of the potentially breaking change of
sending exception reports asynchronously by default.

### Bug Fixes

* Fix potential `NullPointerException` when passing `null` to `Bugsnag.notify()`
  [Delisa Mason](https://github.com/kattrali)
  [#38](https://github.com/bugsnag/bugsnag-java/pull/38)

### Enhancements

* Send payloads to Bugsnag asynchronously by default
  [Mike Bull](https://github.com/bullmo)
  [#36](https://github.com/bugsnag/bugsnag-java/pull/36)

## 1.4.0 (9 Mar 2016)

### Bug Fixes

* Truncate large payloads to avoid Bad Request errors due to size
  [Duncan Hewett](https://github.com/duncanhewett)
  [#35](https://github.com/bugsnag/bugsnag-java/pull/35)

### Enhancements

* Use TLS by default when connecting to Bugsnag
  [Duncan Hewett](https://github.com/duncanhewett)
  [#34](https://github.com/bugsnag/bugsnag-java/pull/34)

## 1.3.0 (28 Jan 2016)

This release includes an update to the `org.json` dependency.

### Enhancements

* Add configurable connection and read timeouts
  [Lauri Lehtinen](https://github.com/llehtinen)
  [#25](https://github.com/bugsnag/bugsnag-java/pull/25)

* Send the hostname with error reports
  [Mike Bull](https://github.com/bullmo)
  [#23](https://github.com/bugsnag/bugsnag-java/issues/23)
  [#30](https://github.com/bugsnag/bugsnag-java/pull/30)

1.2.8
-----
-   Revert method chaining support in Client

1.2.7
-----
-   Add support for sending thread state information with `setSendThreads`
-   Allow chaining of methods on Client

1.2.6
-----
-   Expose `beforeNotify` callback stack to child classes

1.2.5
-----
-   Add support for `beforeNotify` callback

1.2.4
-----
-   Remove hostname as it causes issues with android

1.2.3
-----
-   Prepare 'severity' feature for release

1.2.2
-----
-   Expose the ExceptionHandler class to allow removal of auto-notification

1.2.1
-----
-   Add hostname to device information

1.2.0
-----
-   Included device and app fields. Transmit appState and deviceState.

1.1.0
-----
-   Reduced memory usage, allow streaming errors from a file

1.0.5
-----
-   Expose the `Configuration` class

1.0.4
-----
-   Added support for `setIgnoreClasses` to set which exception classes
    should not be sent to Bugsnag.

1.0.3
-----
-   Fixed compatibility with Java 1.5

1.0.2
-----
-   Add metrics tracking

1.0.1
-----
-   Allow changing of notifier name/version
-   Reduced jar size

1.0.0
-----
-   Initial release
