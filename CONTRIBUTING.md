## Contributing

1. [Fork](https://help.github.com/articles/fork-a-repo) the
   [library on GitHub](https://github.com/bugsnag/bugsnag-java)
2. [Build](#building) and [test](#testing) your changes
3. Commit and push until you are happy with your contribution
4. [Make a pull request](https://help.github.com/articles/using-pull-requests)

Thanks!

## Building

```
./gradlew jar
```

## Testing

Runs tests and checkstyle.

```
./gradlew check
```

## Making a Release


### 1. Ensure you have permission to make a release

Create a Sonatype account:

1. Create a [Sonatype JIRA](https://issues.sonatype.org) account
1. Ask in the [Bugsnag Sonatype JIRA ticket](https://issues.sonatype.org/browse/OSSRH-5533) to become a contributor
1. Ask an existing contributor (likely Simon) to confirm in the ticket
1. Wait for Sonatype them to confirm the approval

### 2. Configure the prerequisites

1. Create your [PGP Signatures](http://central.sonatype.org/pages/working-with-pgp-signatures.html)
2. Configure your `~/.gradle/gradle.properties`:

   ```xml
   signing.keyId=your-gpg-key-id (8-character hex)
   signing.password=your-gpg-password
   signing.secretKeyRingFile=~/.gnupg/secring.gpg
   
   sonatypeUsername=your-sonatype-username
   sonatypePassword=your-sonatype-password
   ```

### 3. Making a release

1. Update the CHANGELOG.md file with any changes
1. Bump the version number in `Notifier.java`
1. Commit the changes
1. Create a release build:
   * `./gradlew clean release`
     - enter the release version (e.g. `1.2.0`)
     - accept the default development version
1. Create a release in GitHub
1. Upload the archives to Sonatype Nexus:
   * `git checkout <TAG_NAME>`
   * `./gradlew clean uploadArchives`
1. "Promote" the release build on Maven Central
   * Go to the [sonatype open source dashboard](https://oss.sonatype.org/index.html#stagingRepositories)
   * Click the search box at the top right, and type “com.bugsnag”
   * Select the com.bugsnag staging repository
   * Click the “Close” button in the toolbar to prompt the repository to be checked
   * Click the “Refresh” button
   * Select the com.bugsnag repository (should have a status of 'closed')
   * Click the “Release” button in the toolbar
1. Upload the new jar to S3
   * Log in to the [AWS Console](https://bugsnag.signin.aws.amazon.com/console)
   * Upload `build/libs/bugsnag-x.x.x.jar` to `bugsnagcdn/bugsnag-java` on S3
	 * Ensure file permissions are set to allow anyone to download (click on the
     file, then "Properties")
1. Update the version numbers on the website:

   ```
   bugsnag-website/config/notifiers.yml
   ```

### 4. Update docs.bugsnag.com

Update the setup guides for Java with any new content, and bump major version
numbers in installation instructions if changed.

