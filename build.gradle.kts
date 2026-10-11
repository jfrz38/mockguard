plugins {
    base
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.jreleaser) apply false
}

val mockguardCheck by tasks.registering {
    group = "verification"
    description = "Checks the MockGuard runtime product."
    dependsOn(
        ":mockguard:check",
        ":mockguard-consumer-tests:runtimeConsumerTest",
        ":mockguard-consumer-tests:runtimePublicationTest",
    )
}

val scannerCheck by tasks.registering {
    group = "verification"
    description = "Checks the MockGuard scanner product."
    dependsOn(
        ":mockguard-scanner:check",
        ":mockguard-consumer-tests:scannerConsumerTest",
        ":mockguard-consumer-tests:scannerPublicationTest",
    )
}

val consumerCheck by tasks.registering {
    group = "verification"
    description = "Runs all consumer compatibility suites."
    dependsOn(
        ":mockguard-consumer-tests:runtimeConsumerTest",
        ":mockguard-consumer-tests:scannerConsumerTest",
        ":mockguard-consumer-tests:runtimePublicationTest",
        ":mockguard-consumer-tests:scannerPublicationTest",
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
