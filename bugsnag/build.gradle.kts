plugins {
    alias(libs.plugins.license)
    `java-library`
}

apply(from = "../common.gradle.kts")

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    api(libs.jackson.databind)
    api(libs.slf4j.api)
    compileOnly(libs.jakarta.servlet.api)
    compileOnly(libs.logback.classic) {
        exclude(group = "org.slf4j")
    }

    testImplementation(libs.junit)
    testImplementation(libs.log4j.over.slf4j)
    testImplementation(libs.jakarta.servlet.api)
    testImplementation(libs.mockito.core)
    testImplementation(libs.logback.classic) {
        exclude(group = "org.slf4j")
    }
}

// license checking
configure<nl.javadude.gradle.plugins.license.LicenseExtension> {
    header = rootProject.file("LICENSE")
    isIgnoreFailures = true
}

tasks.named("downloadLicenses") {
    // Note: dependencyConfiguration property needs to be set through the plugin's DSL
    // This may require checking the plugin documentation for Kotlin DSL syntax
}

java {
    withJavadocJar()
}

/** ---- Publishing config ----
 * Pulls in publishing+signing rules from the shared release.gradle.kts.
 * This will create tasks like:
 *   :bugsnag:publishMavenJavaPublicationToTestRepository
 *   :bugsnag:publishMavenJavaPublicationToOssrhStagingRepository
 */
apply(from = "${rootProject.projectDir}/release.gradle.kts")

