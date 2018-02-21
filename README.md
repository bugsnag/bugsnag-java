# Bugsnag exception reporter for Java
[![Documentation](https://img.shields.io/badge/docs-latest-blue.svg)](https://docs.bugsnag.com/platforms/java)
[![Build status](https://travis-ci.org/bugsnag/bugsnag-java.svg?branch=master)](https://travis-ci.org/bugsnag/bugsnag-java)

The Bugsnag exception reporter for Java automatically detects and reports errors and exceptions in your Java code. Learn more about [reporting Java exceptions](https://www.bugsnag.com/platforms/java/) with Bugsnag.

## Features

* Automatically report unhandled exceptions and crashes
* Report handled exceptions
* Attach custom diagnostic data to determine how many people are affected by a crash and steps to reproduce the error

## Getting started

### Spring

1. [Create a Bugsnag account](https://www.bugsnag.com)
2. Complete the instructions in the [integration guide](https://docs.bugsnag.com/platforms/java/spring)
3. Report handled exceptions using [`Bugsnag.notify()`](https://docs.bugsnag.com/platforms/java/spring/#reporting-handled-exceptions)
4. Customize your integration using the [configuration options](https://docs.bugsnag.com/platforms/java/spring/configuration-options/)

### Other Java apps

1. [Create a Bugsnag account](https://www.bugsnag.com)
2. Complete the instructions in the [integration guide](https://docs.bugsnag.com/platforms/java/other)
3. Report handled exceptions using [`Bugsnag.notify()`](https://docs.bugsnag.com/platforms/java/other/#reporting-handled-exceptions)
4. Customize your integration using the [configuration options](https://docs.bugsnag.com/platforms/java/other/configuration-options/)

## Support

* Check out the configuration options for [Spring](https://docs.bugsnag.com/platforms/java/spring/configuration-options/) or [other Java apps](https://docs.bugsnag.com/platforms/java/other/configuration-options/)
* [Search open and closed issues](https://github.com/bugsnag/bugsnag-java/issues?q=is%3Aissue) for similar problems
* [Report a bug or request a feature](https://github.com/bugsnag/bugsnag-java/issues/new)

## Contributing

All contributors are welcome! For information on how to build, test, and release
`bugsnag-java`, see our [contributing guide](https://github.com/bugsnag/bugsnag-java/blob/master/CONTRIBUTING.md).

## License

The Bugsnag Java library is free software released under the MIT License. See [LICENSE.txt](https://github.com/bugsnag/bugsnag-java/blob/master/LICENSE.txt) for details.
