plugins {
    base
    kotlin("jvm") version "2.4.10" apply false
    id("org.jreleaser") version "1.25.0" apply false
}

val mockguardCheck by tasks.registering {
    group = "verification"
    description = "Checks the MockGuard runtime product."
    dependsOn(
        ":mockguard:check",
        ":mockguard-consumer-tests:runtimeConsumerTest",
    )
}

val scannerCheck by tasks.registering {
    group = "verification"
    description = "Checks the MockGuard scanner product."
    dependsOn(
        ":mockguard-scanner:check",
        ":mockguard-consumer-tests:scannerConsumerTest",
    )
}

val consumerCheck by tasks.registering {
    group = "verification"
    description = "Runs all consumer compatibility suites."
    dependsOn(
        ":mockguard-consumer-tests:runtimeConsumerTest",
        ":mockguard-consumer-tests:scannerConsumerTest",
    )
}

val allCheck by tasks.registering {
    group = "verification"
    description = "Checks all products and consumer compatibility."
    dependsOn(mockguardCheck, scannerCheck)
}

tasks.named("check") {
    dependsOn(allCheck)
}
