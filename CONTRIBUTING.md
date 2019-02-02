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

## Installing/testing against a local maven repository

Sometimes its helpful to build and install the bugsnag-java libraries into a
local repository and test the entire dependency flow inside of a sample
application.

To get started:

1. In the `bugsnag-java` directory, run
   `./gradlew -Preleasing=true publishToMavenLocal`.
   This installs `bugsnag-java` and `bugsnag-spring` into your local
   maven repository.
2. In your sample application `build.gradle`, add `mavenLocal()` to the *top* of
   your `allprojects` repositories section:

   ```groovy
   allprojects {
     repositories {
       mavenLocal()
       // other repos as needed
     }
   }
   ```
3. In your sample application `app/build.gradle`, add the following to the
   dependencies section, inserting the exact version number required:

   ```groovy
   dependencies {
     implementation 'com.bugsnag:bugsnag:[VERSION NUMBER]'
   }
   ```
4. Clean your sample application and reload dependencies *every time* you
   rebuild/republish the local dependencies:

   ```
   ./gradlew clean --refresh-dependencies
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
1. Request access to the [Bugsnag organization](https://bintray.com/bugsnag)

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

#### Pre-release Checklist
- [ ] Does the build pass on the CI server?
- [ ] Are all Docs PRs ready to go?
- [ ] Has all new functionality been manually tested on a release build?
  - [ ] Ensure the example app sends an unhandled error
  - [ ] Ensure the example app sends a handled error
- [ ] Have the installation instructions been updated on the [dashboard](https://github.com/bugsnag/bugsnag-website/tree/master/app/views/dashboard/projects/install) as well as the [docs site](https://github.com/bugsnag/docs.bugsnag.com)?
- [ ] Do the installation instructions work for a manual integration?

#### Making the release
1. Merge any remaining PRs to master, ensuring the commit message matches the release tag (e.g. v4.0.0)
1. Update the CHANGELOG.md file with any changes
1. Update the version number by running `make VERSION=[number] bump`
1. Commit the changes
1. Create a release build:
   * `./gradlew -Preleasing=true clean release`
     - enter the release version (e.g. `1.2.0`)
     - accept the default development version
1. Create a release in GitHub, attaching the changelog entry and build artifacts
1. Upload the archives to Sonatype Nexus and Bintray:
   * `./gradlew -Preleasing=true uploadArchives bintrayUpload`
1. "Promote" the build on Maven Central:
   * `./gradlew -Preleasing=true closeAndReleaseRepository`
1. Update the documentation (integration guide, quick start):
   * Update the version numbers of the dependencies listed in the manual
     integration guide.
   * For a major version change, update the version numbers in the integration
     instructions on docs.bugsnag.com and the quick start guides on the website.

#### Post-release Checklist
- [ ] Have all Docs PRs been merged?
- [ ] Do the existing example apps send an error report using the released artifact?
- [ ] Make releases to downstream libraries, if appropriate (generally for bug fixes)
