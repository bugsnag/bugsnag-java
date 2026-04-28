buildscript {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
}

gradle.projectsEvaluated {
    tasks.withType<JavaCompile>().configureEach {
        // fail build on warnings, disable options complaining about Java 6 compatibility when building with JDK 7+
        options.compilerArgs.addAll(listOf("-Xlint:all", "-Werror", "-Xlint:-options"))
    }
}

tasks.register("publishToTestRepoAll") {
    dependsOn(subprojects.mapNotNull {
        tasks.findByPath("${it.path}:publishAllPublicationsToTestRepository")
    })
}

