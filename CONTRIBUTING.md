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

Create a [Bintray](https://bintray.com) account:

1. Create an account
1. Request access to the Bugsnag organization

### 2. Configure the prerequisites

1. Create your [PGP Signatures](http://central.sonatype.org/pages/working-with-pgp-signatures.html)
2. Configure your `~/.gradle/gradle.properties`:

   ```ini
   signing.keyId=your-gpg-key-id # (8-character hex)
   signing.password=your-gpg-password
   signing.secretKeyRingFile=/PATH/TO/HOME/.gnupg/secring.gpg

   nexusUsername=your-sonatype-username
   nexusPassword=your-sonatype-password

   # Your credentials for https://bintray.com
   bintray_user=your-bintray-username
   bintray_api_key=your-bintray-api-key
   ```

### 3. Making a release

1. Update the CHANGELOG.md file with any changes
1. Update the version number by running `make VERSION=[number] bump`
1. Commit the changes
1. Create a release build:
   * `./gradlew -Preleasing=true clean release`
     - enter the release version (e.g. `1.2.0`)
     - accept the default development version
1. Create a release in GitHub
1. Upload the archives to Sonatype Nexus and Bintray:
   * `./gradlew -Preleasing=true uploadArchives bintrayUpload`
1. "Promote" the build on Maven Central:
   * `./gradlew -Preleasing=true closeAndReleaseRepository`
1. For a major version change, update the version numbers in the integration instructions in the website.
1. Update docs.bugsnag.com with any new content, and bump major version
numbers in installation instructions if changed. Update the version numbers of
the dependent libraries in the manual integration guide.

