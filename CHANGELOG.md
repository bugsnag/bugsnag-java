# Changelog

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
