plugins {
    kotlin("jvm")
    `java-library`
    application
    `maven-publish`
    signing
}

group = "io.github.jfrz38"
version = "0.1.0"

repositories {
    mavenCentral()
}

val byteBuddyVersion = "1.18.8"

dependencies {
    implementation("net.bytebuddy:byte-buddy:$byteBuddyVersion")

    testImplementation("org.junit.jupiter:junit-jupiter:5.12.2")
    testImplementation("org.mockito:mockito-core:5.23.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    testImplementation("net.bytebuddy:byte-buddy-agent:$byteBuddyVersion")
    testImplementation("org.apache.groovy:groovy:4.0.24")
    testImplementation(kotlin("test"))
    testImplementation(project(":mockguard"))
}

java {
    withSourcesJar()
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

val fatJar = tasks.register<Jar>("fatJar") {
    archiveBaseName = "mockguard-scanner"
    archiveClassifier = "cli"
    dependsOn(configurations.runtimeClasspath)
    from(configurations.runtimeClasspath.get().map { zipTree(it) })
    from(sourceSets.main.get().output)
    manifest {
        attributes("Main-Class" to "com.mockguard.scanner.MainKt")
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(fatJar)

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
    val signingKey = findProperty("signingKey") as String?
    val signingPassword = findProperty("signingPassword") as String?
    if (!signingKey.isNullOrBlank() && !signingPassword.isNullOrBlank()) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications)
    }
}

kotlin {
    jvmToolchain(17)
}
