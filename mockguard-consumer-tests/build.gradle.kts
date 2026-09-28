plugins {
    kotlin("jvm")
    `java-library`
    `jvm-test-suite`
}

evaluationDependsOn(":mockguard")
evaluationDependsOn(":mockguard-scanner")

repositories {
    maven {
        name = "mockguardStaging"
        url = uri(project(":mockguard").layout.buildDirectory.dir("staging-deploy").get().asFile)
    }
    maven {
        name = "scannerStaging"
        url = uri(project(":mockguard-scanner").layout.buildDirectory.dir("staging-deploy").get().asFile)
    }
    mavenCentral()
}

val scannerCli by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true

    attributes {
        attribute(
            org.gradle.api.attributes.Category.CATEGORY_ATTRIBUTE,
            objects.named(org.gradle.api.attributes.Category.LIBRARY),
        )
        attribute(
            org.gradle.api.attributes.Usage.USAGE_ATTRIBUTE,
            objects.named(org.gradle.api.attributes.Usage.JAVA_RUNTIME),
        )
        attribute(
            org.gradle.api.attributes.Bundling.BUNDLING_ATTRIBUTE,
            objects.named(org.gradle.api.attributes.Bundling.SHADOWED),
        )
        attribute(
            org.gradle.api.attributes.LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE,
            objects.named(org.gradle.api.attributes.LibraryElements.JAR),
        )
    }
}

val publishedScannerRuntime by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    add(
        scannerCli.name,
        project(
            path = ":mockguard-scanner",
            configuration = "scannerCliElements",
        ),
    )
    add(
        publishedScannerRuntime.name,
        "io.github.jfrz38:mockguard-scanner:${project(":mockguard-scanner").version}",
    )
}

testing {
    suites {
        register<JvmTestSuite>("runtimeConsumerTest") {
            useJUnitJupiter(libs.versions.junit.get())

            dependencies {
                implementation(project(":mockguard"))
                implementation(platform(libs.junit.bom))
                implementation(libs.junit.jupiter.params)
                implementation(libs.junit.platform.launcher)
                implementation(libs.mockito.core)
                implementation(libs.mockito.junit.jupiter)
                implementation(libs.mockito.kotlin)
                implementation(libs.kotlin.test.junit5)
            }

            targets.all {
                testTask.configure {
                    filter {
                        excludeTestsMatching("com.mockguard.consumer.fixtures.*")
                    }
                }
            }
        }

        register<JvmTestSuite>("scannerConsumerTest") {
            useJUnitJupiter(libs.versions.junit.get())

            dependencies {
                implementation(project(":mockguard"))
                implementation(libs.mockito.core)
                implementation(libs.kotlin.test.junit5)
            }

            targets.all {
                testTask.configure {
                    inputs.files(scannerCli)
                        .withPropertyName("scannerCli")
                        .withPathSensitivity(PathSensitivity.NONE)

                    doFirst {
                        val artifacts = scannerCli.files
                        require(artifacts.size == 1) {
                            "Expected one scanner CLI artifact, found: $artifacts"
                        }
                        systemProperty(
                            "mockguard.scanner.jar",
                            artifacts.single().absolutePath,
                        )
                    }

                    filter {
                        excludeTestsMatching("com.mockguard.consumer.fixtures.*")
                    }
                }
            }
        }

        register<JvmTestSuite>("runtimePublicationTest") {
            useJUnitJupiter(libs.versions.junit.get())

            dependencies {
                implementation("io.github.jfrz38:mockguard:${project(":mockguard").version}")
                implementation(platform(libs.junit.bom))
                implementation(libs.junit.jupiter.params)
                implementation(libs.junit.platform.launcher)
                implementation(libs.mockito.core)
                implementation(libs.mockito.junit.jupiter)
                implementation(libs.mockito.kotlin)
                implementation(libs.kotlin.test.junit5)
            }

            targets.all {
                testTask.configure {
                    filter {
                        excludeTestsMatching("com.mockguard.consumer.fixtures.*")
                    }
                }
            }
        }

        register<JvmTestSuite>("scannerPublicationTest") {
            useJUnitJupiter(libs.versions.junit.get())

            dependencies {
                implementation(project(":mockguard"))
                implementation(libs.mockito.core)
                implementation(libs.kotlin.test.junit5)
            }

            targets.all {
                testTask.configure {
                    inputs.files(publishedScannerRuntime)
                        .withPropertyName("publishedScannerRuntime")
                        .withPathSensitivity(PathSensitivity.NONE)

                    doFirst {
                        systemProperty(
                            "mockguard.scanner.classpath",
                            publishedScannerRuntime.asPath,
                        )
                    }

                    filter {
                        excludeTestsMatching("com.mockguard.consumer.fixtures.*")
                    }
                }
            }
        }
    }
}

sourceSets.named("runtimePublicationTest") {
    java.setSrcDirs(
        listOf(
            "src/runtimeConsumerTest/java",
            "src/runtimePublicationTest/java",
        ),
    )
    kotlin.setSrcDirs(listOf("src/runtimeConsumerTest/kotlin"))
    kotlin.exclude("**/JavaConsumerIntegrationTest.kt")
}

sourceSets.named("scannerPublicationTest") {
    java.setSrcDirs(listOf("src/scannerConsumerTest/java"))
    kotlin.setSrcDirs(listOf("src/scannerConsumerTest/kotlin"))
}

tasks.named("compileRuntimePublicationTestJava") {
    dependsOn(":mockguard:publishAllPublicationsToStagingRepository")
}

tasks.named("compileRuntimePublicationTestKotlin") {
    dependsOn(":mockguard:publishAllPublicationsToStagingRepository")
}

tasks.named("compileScannerPublicationTestJava") {
    dependsOn(":mockguard-scanner:publishAllPublicationsToStagingRepository")
}

tasks.named("compileScannerPublicationTestKotlin") {
    dependsOn(":mockguard-scanner:publishAllPublicationsToStagingRepository")
}

tasks.named("check") {
    dependsOn(
        "runtimeConsumerTest",
        "scannerConsumerTest",
        "runtimePublicationTest",
        "scannerPublicationTest",
    )
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

kotlin {
    jvmToolchain(17)
}
