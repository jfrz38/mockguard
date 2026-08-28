plugins {
    kotlin("jvm")
    `java-library`
    application
    `maven-publish`
    id("org.jreleaser")
}

import org.jreleaser.model.Active
import org.jreleaser.model.Http

group = "io.github.jfrz38"
version = "0.1.0"

val publicationNamespace = group.toString()

repositories {
    mavenCentral()
}

val byteBuddyVersion = "1.18.11"

dependencies {
    implementation("net.bytebuddy:byte-buddy:$byteBuddyVersion")

    testImplementation("org.junit.jupiter:junit-jupiter:6.1.3")
    testImplementation("org.mockito:mockito-core:5.23.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:6.3.0")
    testImplementation("net.bytebuddy:byte-buddy-agent:$byteBuddyVersion")
    testImplementation("org.apache.groovy:groovy:5.0.8")
    testImplementation(kotlin("test"))
}

java {
    withSourcesJar()
}

val emptyJavadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from(rootProject.file("README.md")) {
        rename { "README.md" }
    }
}

application {
    mainClass = "com.mockguard.scanner.MainKt"
    applicationName = "mockguard-scanner"
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

val runtimeClasspath = configurations.runtimeClasspath

val fatJar = tasks.register<Jar>("fatJar") {
    group = "distribution"
    description = "Builds the executable scanner CLI JAR."
    archiveBaseName.set("mockguard-scanner")
    archiveClassifier.set("cli")
    dependsOn(runtimeClasspath)
    from(sourceSets.main.map { it.output })
    from({
        runtimeClasspath.get().map { dependency ->
            if (dependency.isDirectory) dependency else zipTree(dependency)
        }
    })
    manifest {
        attributes("Main-Class" to application.mainClass.get())
    }
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

val scannerCliElements by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false

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

    outgoing.artifact(fatJar.flatMap { it.archiveFile }) {
        type = "jar"
        builtBy(fatJar)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(emptyJavadocJar)

            pom {
                name.set("mockguard-scanner")
                description.set("Static bytecode scanner for unverified Mockito mocks.")
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
