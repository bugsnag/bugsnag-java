include(":bugsnag")
include(":bugsnag-spring")

include(":examples:simple")
include(":examples:spring")
include(":examples:spring-web")
include(":examples:logback")

// jakarta servlet example requires java 11 compatibility for gretty plugin
if (JavaVersion.current().isJava11Compatible) {
    include(":examples:servlet-jakarta")
}

