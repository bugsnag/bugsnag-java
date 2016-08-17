## Contributing

1. [Fork](https://help.github.com/articles/fork-a-repo) the [notifier on github](https://github.com/bugsnag/bugsnag-java)
2. Build and [test](#testing) your changes
3. Commit and push until you are happy with your contribution
4. [Make a pull request](https://help.github.com/articles/using-pull-requests)

Thanks!

## Testing

Building and running the test requires [Maven](https://maven.apache.org). To
test, run:

```
mvn test
```

## Making a Release

### 1. Ensure you have permission to make a release

This process is a little ridiculous.

1. Create a [Sonatype JIRA](https://issues.sonatype.org) account
2. Ask in the [Bugsnag Sonatype JIRA ticket](https://issues.sonatype.org/browse/OSSRH-5533) to become a contributor
3. Ask an existing contributor (likely Simon) to confirm in the ticket
4. Wait for Sonatype them to confirm the approval

### 2. Configure the prerequisites

1. [Create your PGP Signatures](http://central.sonatype.org/pages/working-with-pgp-signatures.html)
2. [Configure your `~/.m2/settings.xml`](http://central.sonatype.org/pages/apache-maven.html):

   ```xml
   <settings>
		<servers>
			<server>
				<id>sonatype-nexus-staging</id>
				<username>your-nexus-username</username>
				<password>your-nexus-password</password>
			</server>
		</servers>
		<profiles>
			<profile>
				<activation>
					<activeByDefault>true</activeByDefault>
				</activation>
				<properties>
					<gpg.keyname>your-gpg-key-name (8-character hex)</gpg.keyname>
					<gpg.passphrase>your-gpg-passphrase (optional, requires XML escaping)</gpg.passphrase>
				</properties>
			</profile>
		</profiles>
	</settings>
   ```

### 3. Making a release

1. Update the CHANGELOG.md file with any changes
2. Bump the version number in `Configuration.java`
3. Commit the changes
4. Create a release build:
   * `mvn release:clean`
   * `mvn release:prepare`
     - enter the release version (e.g. `1.2.0`)
     - enter the release tag (e.g. `v1.2.0`)
     - accept the default development version
     - enter your GPG password
   * `mvn release:perform`
5. "Promote" the release build on Maven Central
   * Go to the [sonatype open source dashboard](https://oss.sonatype.org/index.html#stagingRepositories)
   * Click the search box at the top right, and type “com.bugsnag”
   * Select the com.bugsnag staging repository
   * Click the “close” button in the toolbar, no message
   * Click the “refresh” button
   * Select the com.bugsnag closed repository
   * Click the “release” button in the toolbar
6. Upload the new jar to S3
   * Log in to the [AWS Console](https://bugsnag.signin.aws.amazon.com/console)
   * Upload `target/bugsnag-x.x.x.jar` to `bugsnagcdn/bugsnag-java` on S3
	 * Ensure file permissions are set to allow anyone to download (click on the
     file, then "Properties")
7. Update the release link in the README.md to the latest version
8. Update the version numbers on the website:

   ```
   bugsnag-website/config/notifiers.yml
   ```

### 4. Update docs.bugsnag.com

Update the setup guides for Java (servers) with any new content.
