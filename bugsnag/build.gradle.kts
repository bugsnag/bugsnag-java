plugins {
    id("com.github.hierynomus.license") version "0.16.1"
    `java-library`
}

apply(from = "../common.gradle.kts")

tasks.named<JavaCompile>("compileJava") {
    sourceCompatibility = "17"
    targetCompatibility = "17"
}

tasks.named<JavaCompile>("compileTestJava") {
    sourceCompatibility = "17"
    targetCompatibility = "17"
}

repositories {
    mavenCentral()
}

val slf4jApiVersion: String by project
val jakartaServletApiVersion: String by project
val logbackVersion: String by project
val junitVersion: String by project
val mockitoVersion: String by project

dependencies {
    api("com.fasterxml.jackson.core:jackson-databind:2.14.1")
    api("org.slf4j:slf4j-api:$slf4jApiVersion")
    compileOnly("jakarta.servlet:jakarta.servlet-api:$jakartaServletApiVersion")
    compileOnly("ch.qos.logback:logback-classic:$logbackVersion") {
        exclude(group = "org.slf4j")
    }

    testImplementation("junit:junit:$junitVersion")
    testImplementation("org.slf4j:log4j-over-slf4j:$slf4jApiVersion")
    testImplementation("jakarta.servlet:jakarta.servlet-api:$jakartaServletApiVersion")
    testImplementation("org.mockito:mockito-core:$mockitoVersion")
    testImplementation("ch.qos.logback:logback-classic:$logbackVersion") {
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

