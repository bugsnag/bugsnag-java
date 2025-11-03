// release.gradle.kts — script plugin (apply from: ...)

apply(plugin = "java")
apply(plugin = "maven-publish")
apply(plugin = "signing")

/** -------- Helpers -------- */
fun projectProperty(k: String): String? = project.findProperty(k) as String?
fun projectBoolean(k: String): Boolean = projectProperty(k)?.toBoolean() ?: false

val isRelease = projectBoolean("releasing") // set with -Preleasing=true on real deploys

// Prefer Gradle properties (works on CI without env vars).
// Put these in ~/.gradle/gradle.properties or project gradle.properties on CI.
val ossrhUser = projectProperty("NEXUS_USERNAME") ?: projectProperty("ossrhUsername")
val ossrhPass = projectProperty("NEXUS_PASSWORD") ?: projectProperty("ossrhPassword")

// Optional in-memory signing keys (recommended for CI)
// signingKey: ASCII-armored private key, signingPassword: key passphrase
val signingKey = projectProperty("signingKey")
val signingPassword = projectProperty("signingPassword")

/** -------- Java / Artifacts -------- */
configure<BasePluginExtension> {
    archivesName.set(projectProperty("artifactId") ?: project.name)
}

configure<JavaPluginExtension> {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    withSourcesJar()
    withJavadocJar()
}

/** -------- Publishing -------- */
configure<PublishingExtension> {
    repositories {
        // Always-available local test repo
        maven {
            name = "test"
            url = uri(rootProject.file("build/repository"))
        }

        // OSSRH only if credentials are available (no envs needed)
        if (ossrhUser != null && ossrhPass != null) {
            maven {
                name = "ossrhStaging"
                url = uri("https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/")
                credentials {
                    username = ossrhUser
                    password = ossrhPass
                }
            }
        } else {
            logger.lifecycle("[publishing] OSSRH credentials not provided; skipping remote repository configuration.")
        }
    }

    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = "com.bugsnag"
            artifactId = projectProperty("artifactId") ?: project.name
            version = rootProject.version.toString()

            pom {
                name.set(projectProperty("projectName") ?: artifactId)
                description.set(projectProperty("projectDescription") ?: "Bugsnag Java Notifier")
                url.set("https://github.com/bugsnag/bugsnag-java")

                scm {
                    url.set("https://github.com/bugsnag/bugsnag-java")
                    connection.set("scm:git:git://github.com/bugsnag/bugsnag-java.git")
                    developerConnection.set("scm:git:ssh://git@github.com/bugsnag/bugsnag-java.git")
                }

                licenses {
                    license {
                        name.set("MIT")
                        url.set("http://opensource.org/licenses/MIT")
                        distribution.set("repo")
                    }
                }

                organization {
                    name.set("Bugsnag")
                    url.set("https://bugsnag.com")
                }

                developers {
                    developer {
                        id.set("loopj")
                        name.set("James Smith")
                        email.set("james@bugsnag.com")
                    }
                }
            }
        }
    }
}

/** -------- Signing (no .asc on test publishes) -------- */
configure<SigningExtension> {
    // Only *require* signing for real releases
    setRequired { isRelease }

    // Only attach signatures when we actually want to sign
    if (isRelease) {
        if (signingKey != null && signingPassword != null) {
            useInMemoryPgpKeys(signingKey, signingPassword)
        }
        sign(the<PublishingExtension>().publications["mavenJava"])
    }
}

/** -------- Safety: never try publishing to OSSRH without creds -------- */
tasks.withType<PublishToMavenRepository>().configureEach {
    onlyIf {
        // Allow all "test" publishes, and only allow OSSRH when creds exist
        repository?.name != "ossrhStaging" || (ossrhUser != null && ossrhPass != null)
    }
}

