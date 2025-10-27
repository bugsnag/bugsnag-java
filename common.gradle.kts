extra["jakartaServletApiVersion"] = "5.0.0"
extra["logbackVersion"] = "1.2.3"
extra["slf4jApiVersion"] = "1.7.25"
extra["junitVersion"] = "4.13.2"
extra["mockitoVersion"] = "5.0.0"
if (JavaVersion.current().isJava8Compatible) {
    apply(plugin = "checkstyle")
}
if (project.hasProperty("releasing") && project.depth <= 1) {
    apply(from = "../release.gradle")
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
