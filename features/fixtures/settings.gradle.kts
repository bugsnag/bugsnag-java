include(":mazerunner")
include(":scenarios")

if (JavaVersion.current() >= JavaVersion.VERSION_17) {
    include(":mazerunnerspringboot3")
    include(":mazerunnerplainspring6")
}

