plugins {
    war
}

group = "com.bugsnag.mazerunnerplainspring"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
    maven {
        url = file("../libs").toURI()
    }
}

dependencies {
    implementation("org.springframework:spring-webmvc:6.0.0")
    implementation("jakarta.servlet:jakarta.servlet-api:5.0.0")
    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("org.codehaus.janino:janino:3.1.10")

    implementation("com.fasterxml.jackson.core:jackson-annotations:2.14.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.1")
    implementation("com.bugsnag:bugsnag:9.9.9-test")
    implementation("com.bugsnag:bugsnag-spring:9.9.9-test")
    implementation(project(":scenarios"))
}

tasks.named<War>("war") {
    archiveFileName.set("mazerunnerplainspring.war")
}

