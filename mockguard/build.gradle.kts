plugins {
    `java-library`
    `maven-publish`
    signing
    id("org.jreleaser") version "1.25.0"
    kotlin("jvm")
}

import org.jreleaser.model.Active
import org.jreleaser.model.Http

group = "io.github.jfrz38"
version = "0.1.1"

val publicationNamespace = group.toString()

repositories {
    mavenCentral()
}

java {
    withSourcesJar()
}

dependencies {
    api("org.junit.jupiter:junit-jupiter-api:6.1.3")
    implementation("org.mockito:mockito-core:5.23.0")
    implementation("net.bytebuddy:byte-buddy:1.18.11")
    implementation("net.bytebuddy:byte-buddy-agent:1.18.11")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:6.1.3")
    testImplementation("org.junit.jupiter:junit-jupiter-params:6.1.3")
    testImplementation("org.mockito:mockito-junit-jupiter:5.23.0")
    testImplementation("org.junit.platform:junit-platform-launcher:6.1.3")
}

val emptyJavadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(rootProject.file("README.md")) {
        rename { "README.md" }
    }
}

tasks.test {
    useJUnitPlatform()
    exclude("**/*$*.class")
    filter {
        excludeTestsMatching("com.mockguard.integration.fixtures.*")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(emptyJavadocJar)

            pom {
                name.set("mockguard")
                description.set("Strict mock verification for JUnit 5 and Mockito.")
                url.set("https://github.com/jfrz38/mockguard")
                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
                developers {
                    developer {
                        id.set("jfrz38")
                        name.set("Jose F. Ruiz Zamora")
                        email.set("jrz899@inlumine.ual.es")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/jfrz38/mockguard.git")
                    developerConnection.set("scm:git:ssh://git@github.com/jfrz38/mockguard.git")
                    url.set("https://github.com/jfrz38/mockguard")
                }
                issueManagement {
                    system.set("GitHub Issues")
                    url.set("https://github.com/jfrz38/mockguard/issues")
                }
            }
        }
    }
    repositories {
        maven {
            name = "staging"
            url = layout.buildDirectory.dir("staging-deploy").get().asFile.toURI()
        }
    }
}

signing {
    val signingKey: String? = findProperty("signingKey") as String?
    val signingPassword: String? = findProperty("signingPassword") as String?

    if (!signingKey.isNullOrBlank() && !signingPassword.isNullOrBlank()) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications)
    }
}

jreleaser {
    gitRootSearch.set(true)
    signing {
        active.set(Active.ALWAYS)
        armored.set(true)
        pgp {
            active.set(Active.ALWAYS)
            armored.set(true)
        }
    }
    deploy {
        maven {
            mavenCentral {
                create("release-deploy") {
                    active.set(Active.RELEASE)
                    url.set("https://central.sonatype.com/api/v1/publisher")
                    authorization.set(Http.Authorization.BEARER)
                    namespace.set(publicationNamespace)
                    snapshotSupported.set(false)
                    applyMavenCentralRules.set(true)
                    retryDelay.set(10)
                    maxRetries.set(6)
                    stagingRepository("build/staging-deploy")
                }
            }
            nexus2 {
                create("snapshot-deploy") {
                    active.set(Active.SNAPSHOT)
                    snapshotUrl.set("https://central.sonatype.com/repository/maven-snapshots/")
                    authorization.set(Http.Authorization.BEARER)
                    snapshotSupported.set(true)
                    closeRepository.set(true)
                    releaseRepository.set(true)
                    applyMavenCentralRules.set(true)
                    stagingRepository("build/staging-deploy")
                }
            }
        }
    }
}

kotlin {
    jvmToolchain(17)
}
