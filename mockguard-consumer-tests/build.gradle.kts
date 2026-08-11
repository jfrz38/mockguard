plugins {
    kotlin("jvm")
    `java-library`
}

evaluationDependsOn(":mockguard-scanner")

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(project(":mockguard"))
    testImplementation("org.junit.jupiter:junit-jupiter-engine:6.0.3")
    testImplementation("org.junit.jupiter:junit-jupiter-params:6.0.3")
    testImplementation("org.junit.platform:junit-platform-launcher:6.0.3")
    testImplementation("org.mockito:mockito-core:5.23.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.23.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation(kotlin("test"))
}

val scannerFatJar = project(":mockguard-scanner").tasks.named<Jar>("fatJar")

tasks.test {
    dependsOn(scannerFatJar)
    inputs.file(scannerFatJar.flatMap { it.archiveFile })

    doFirst {
        systemProperty(
            "mockguard.scanner.jar",
            scannerFatJar.get().archiveFile.get().asFile.absolutePath,
        )
    }

    useJUnitPlatform()
    filter {
        excludeTestsMatching("com.mockguard.consumer.fixtures.*")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

kotlin {
    jvmToolchain(17)
}
