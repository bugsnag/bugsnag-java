Bugsnag Notifier for Java
=========================

The Bugsnag Notifier for Java gives you instant notification of exceptions
thrown from your Java applications.
The notifier hooks into `Thread.UncaughtExceptionHandler`, so any
uncaught exceptions in your app will be sent to your Bugsnag dashboard.

[Bugsnag](https://bugsnag.com) captures errors in real-time from your websites
and mobile applications, helping you to understand and resolve them
as fast as possible. [Create a free account](https://bugsnag.com) to start
capturing errors from your applications.


Installation & Setup
--------------------

-   Download the [latest bugsnag.jar](http://bugsnagcdn.s3.amazonaws.com/bugsnag-java/bugsnag-1.2.0.jar)
    and place it in your app's classpath.

    Bugsnag for Java depends only on the `org.json` library,
    [download the jar here](http://repo1.maven.org/maven2/org/json/json/20090211/json-20090211.jar).

    *Note: if your project uses [Maven](http://maven.apache.org/) you can 
    instead [add bugsnag as a dependency](http://mvnrepository.com/artifact/com.bugsnag/bugsnag)
    in your `pom.xml`.*

-   Import the Bugsnag `Client` class in your code and create an instance to 
    begin capturing exceptions:

    ```java
    import com.bugsnag.Client;
    Client bugsnag = new Client("your-api-key-goes-here");
    ```


Sending Custom Data With Exceptions
-----------------------------------

It is often useful to send additional meta-data about your app, such as
information about the currently logged in user, along with any exceptions,
to help debug problems. To add custom data to every exception you can
use `addToTab`:

```java
bugsnag.addToTab("User", "Name", "Bob Hoskins");
bugsnag.addToTab("User", "Paying Customer?", true);
```


Send Non-Fatal Exceptions to Bugsnag
------------------------------------

If you would like to send non-fatal exceptions to Bugsnag, you can pass any
`Throwable` object to the `notify` method:

```java
bugsnag.notify(new RuntimeException("Non-fatal"));
```

You can also send additional meta-data with your exception:

```java
import com.bugsnag.MetaData;

MetaData metaData = new MetaData();
metaData.addToTab("User", "username", "bob-hoskins");
metaData.addToTab("User", "email", "bob@example.com");

bugsnag.notify(new RuntimeException("Non-fatal"), metaData);
```


Configuration
-------------

###setContext

Bugsnag uses the concept of "contexts" to help display and group your
errors. Contexts represent what was happening in your application at the
time an error occurs.

```java
bugsnag.setContext("MyActivity");
```

###setUser

Bugsnag helps you understand how many of your users are affected by each
error. In order to do this, we need to send along user information with every
exception.

If you would like to enable this, set the `user`. You can set the user id,
which should be the unique id to represent that user across all your apps,
the user's email address and the user's name:

```java
bugsnag.setUser("userId", "user@email.com", "User Name");
```

###setReleaseStage

If you would like to distinguish between errors that happen in different
stages of the application release process (development, production, etc)
you can set the `releaseStage` that is reported to Bugsnag.

```java
bugsnag.setReleaseStage("development");
```

By default this is set to be "production".

###setNotifyReleaseStages

By default, we will notify Bugsnag of exceptions that happen in any
`releaseStage`. If you would like to change which release stages notify
Bugsnag of exceptions you can call `setNotifyReleaseStages`:

```java
bugsnag.setNotifyReleaseStages(new String[]{"production", "development"});
```

###setAutoNotify

By default, we will automatically notify Bugsnag of any fatal exceptions
in your application. If you want to stop this from happening, you can call
`setAutoNotify`:

```java
bugsnag.setAutoNotify(false);
```

###setFilters

Sets the strings to filter out from the `extraData` maps before sending
them to Bugsnag. Use this if you want to ensure you don't send
sensitive data such as passwords, and credit card numbers to our
servers. Any keys which contain these strings will be filtered.

```java
bugsnag.setFilters(new String[]{"password", "credit_card_number"});
```

By default, `filters` is set to `new String[] {"password"};`

###setProjectPackages

Sets which package names Bugsnag should consider as "inProject". We mark 
stacktrace lines as in-project if they originate from any of these
packages.

```java
bugsnag.setProjectPackages("com.company.package1", "com.company.package2");
```

###setIgnoreClasses

Sets for which exception classes we should not send exceptions to Bugsnag.

```java
bugsnag.setIgnoreClasses("java.io.IOException", "com.example.Custom");
```


Reporting Bugs or Feature Requests
----------------------------------

Please report any bugs or feature requests on the github issues page for this
project here:

<https://github.com/bugsnag/bugsnag-java/issues>


Contributing
------------

-   [Fork](https://help.github.com/articles/fork-a-repo) the 
    [notifier on github](https://github.com/bugsnag/bugsnag-java)
-   Commit and push until you are happy with your contribution
-   Run the tests with `mvn test`.
-   [Make a pull request](https://help.github.com/articles/using-pull-requests)
-   Thanks!


License
-------

The Bugsnag Java notifier is free software released under the MIT License.
See [LICENSE.txt](https://github.com/bugsnag/bugsnag-java/blob/master/LICENSE.txt)
for details.
