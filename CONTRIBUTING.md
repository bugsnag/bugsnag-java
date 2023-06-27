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

### Unit tests and checkstyle

```
./gradlew check
```

### End-to-end tests

These tests are implemented with our notifier testing tool [Maze runner](https://github.com/bugsnag/maze-runner).

End to end tests are written in cucumber-style `.feature` files, and need Ruby-backed "steps" in order to know what to run. The tests are located in the top level [`features`](/features/) directory.

```
bundle install
bundle exec bugsnag-maze-runner
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

### 2. Configure the prerequisites

1. Create your [PGP Signatures](http://central.sonatype.org/pages/working-with-pgp-signatures.html)
2. Configure your `~/.gradle/gradle.properties`:

   ```ini
   signing.keyId=your-gpg-key-id # (8-character hex)
   signing.password=your-gpg-password
   signing.secretKeyRingFile=/PATH/TO/HOME/.gnupg/secring.gpg

   nexusUsername=your-sonatype-username
   nexusPassword=your-sonatype-password
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
To start a release:

- decide on a version number
- create a new release branch from `next` with the version number in the branch name
  `git checkout -b release/vX.Y.Z`
- Pull the release branch and update it locally:
    - [ ] Update the version number with `make VERSION=[number] bump`
    - [ ] Update the version number and date in the changelog
    - [ ] Inspect the updated CHANGELOG, and version files to ensure they are correct
- Commit the changes with the release tag as the commit message (e.g. v4.0.0)
- make a PR from your release branch to `master` entitled `Release vX.Y.Z`
- Get the release PR reviewed – all code changes should have been reviewed already, this should be a review of the integration of all changes to be shipped and the changelog
- Once merged:
    - Pull the latest changes (checking out `master` if necessary)
    - Create a release build and upload to sonatype:
        - `./gradlew -Preleasing=true clean publishAllPublicationsToSonatypeRepository`
        - Verify that the artefacts are uploaded to sonatype.
        - Test the Sonatype artefacts in the example app by adding the newly created 'combugsnag-XXXX' repository to the build.gradle: maven {url "https://oss.sonatype.org/service/local/repositories/combugsnag-XXXX/content/"}
    - Release to GitHub:
        - [ ] Create *and tag* the release from `master` on [GitHub Releases](https://github.com/bugsnag/bugsnag-android/releases), attaching the changelog entry and build artifacts
    - Checkout `master` and pull the latest changes
    - "Promote" the release build on Maven Central:
        - Go to the [sonatype open source dashboard](https://oss.sonatype.org/index.html#stagingRepositories)
        - Click the search box at the top right, and type “com.bugsnag”
        - Select the com.bugsnag staging repository
        - Ensure that JARs, POMs and JAVADOCs are present for each module
        - Click the “close” button in the toolbar, no message
        - Click the “refresh” button
        - Select the com.bugsnag closed repository
        - Click the “release” button in the toolbar
    - Merge outstanding docs PRs related to this release

#### Post-release Checklist
- [ ] Have all Docs PRs been merged?
- [ ] Do the existing example apps send an error report using the released artifact?
- [ ] Make releases to downstream libraries, if appropriate (generally for bug fixes)
