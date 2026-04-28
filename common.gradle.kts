// Version catalog is now used - see gradle/libs.versions.toml
apply(plugin = "checkstyle")

tasks.named<Test>("test") {
    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
    }
}

configure<org.gradle.api.plugins.quality.CheckstyleExtension> {
    toolVersion = "8.18"
    configFile = file("${rootDir}/config/checkstyle/checkstyle.xml")
}
