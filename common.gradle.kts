// Version catalog is now used - see gradle/libs.versions.toml
if (JavaVersion.current().isJava8Compatible) {
    apply(plugin = "checkstyle")
}
if (project.hasProperty("releasing") && project.depth <= 1) {
    apply(from = "../release.gradle.kts")
}
tasks.named<Test>("test") {
    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
    }
}
if (JavaVersion.current().isJava8Compatible) {
    configure<org.gradle.api.plugins.quality.CheckstyleExtension> {
        toolVersion = "8.18"
        configFile = file("${rootDir}/config/checkstyle/checkstyle.xml")
    }
}
