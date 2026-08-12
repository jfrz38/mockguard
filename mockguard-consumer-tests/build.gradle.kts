plugins {
    kotlin("jvm")
    `java-library`
    `jvm-test-suite`
}

repositories {
    mavenCentral()
}

val scannerCli by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
    isVisible = false

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

dependencies {
    add(
        scannerCli.name,
        project(
            path = ":mockguard-scanner",
            configuration = "scannerCliElements",
        ),
    )
}

testing {
    suites {
        register<JvmTestSuite>("runtimeConsumerTest") {
            useJUnitJupiter("6.0.3")

            dependencies {
                implementation(project(":mockguard"))
                implementation("org.junit.jupiter:junit-jupiter-params:6.0.3")
                implementation("org.junit.platform:junit-platform-launcher:6.0.3")
                implementation("org.mockito:mockito-core:5.23.0")
                implementation("org.mockito:mockito-junit-jupiter:5.23.0")
                implementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
                implementation(kotlin("test"))
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
            useJUnitJupiter("6.0.3")

            dependencies {
                implementation(project(":mockguard"))
                implementation("org.mockito:mockito-core:5.23.0")
                implementation(kotlin("test"))
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
    }
}

tasks.named("check") {
    dependsOn("runtimeConsumerTest", "scannerConsumerTest")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

kotlin {
    jvmToolchain(17)
}
