plugins {
    `java-library`
    `maven-publish`
    signing
    kotlin("jvm") version "2.0.20"
}

group = "io.github.jfrz38"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    withSourcesJar()
}

dependencies {
    api("org.junit.jupiter:junit-jupiter-api:5.10.2")
    implementation("org.mockito:mockito-core:5.11.0")
    implementation("net.bytebuddy:byte-buddy:1.14.18")
    implementation("net.bytebuddy:byte-buddy-agent:1.14.18")

    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation("org.mockito:mockito-junit-jupiter:5.11.0")
    testImplementation("org.junit.platform:junit-platform-launcher:1.10.2")
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
}

signing {
    val signingKey: String? = findProperty("signingKey") as String?
    val signingPassword: String? = findProperty("signingPassword") as String?

    if (!signingKey.isNullOrBlank() && !signingPassword.isNullOrBlank()) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications)
    }
}

kotlin {
    jvmToolchain(17)
}
