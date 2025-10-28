plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

java {
    withJavadocJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

val sourceJar by tasks.registering(Jar::class) {
    from(sourceSets.main.get().allJava)
}

// do not move this higher - sourceJar must be registered before we apply common.gradle.kts
apply(from = "../common.gradle.kts")

dependencies {
    implementation(project(":bugsnag"))

    implementation(libs.logback.core)
    implementation(libs.slf4j.api)

    implementation(libs.jakarta.servlet.api)
    implementation(libs.spring.webmvc)
    implementation(libs.springBoot3.boot)
    implementation(libs.spring.aop)

    testImplementation(project(":bugsnag").dependencyProject.sourceSets["test"].output)
    testImplementation(project(":bugsnag"))
    testImplementation(libs.junit)
    testImplementation(libs.springBoot3.starter.test)
    testImplementation(libs.springBoot3.starter.web)
    testImplementation(libs.junit.jupiter)
    testCompileOnly(libs.mockito.core.legacy)
}

/** ---- Publishing config ----
 * Pulls in publishing+signing rules from the shared release.gradle.kts.
 * This will create tasks like:
 *   :bugsnag:publishMavenJavaPublicationToTestRepository
 *   :bugsnag:publishMavenJavaPublicationToOssrhStagingRepository
 */
apply(from = "${rootProject.projectDir}/release.gradle.kts")

