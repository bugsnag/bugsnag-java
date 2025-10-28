plugins {
    java
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.0"
}

group = "com.bugsnag.mazerunner"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    maven {
        url = file("../libs").toURI()
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("ch.qos.logback:logback-core:1.5.18")
    implementation("org.codehaus.janino:janino:3.1.10")

    implementation("com.fasterxml.jackson.core:jackson-annotations:2.14.1")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.14.1")
    implementation("com.bugsnag:bugsnag:9.9.9-test")
    implementation(project(":scenarios"))

    testImplementation("junit:junit:4.13.2")
}

