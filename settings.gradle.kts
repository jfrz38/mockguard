plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "mockguard-root"

include(":mockguard")
include(":mockguard-scanner")
include(":mockguard-consumer-tests")
