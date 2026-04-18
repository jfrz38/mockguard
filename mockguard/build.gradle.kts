plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "2.0.20"
}

group = "io.github.jfrz38"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    withSourcesJar()
    withJavadocJar()
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

tasks.test {
    useJUnitPlatform()
    exclude("**/*$*.class")
    filter {
        excludeTestsMatching("com.mockguard.integration.fixtures.*")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

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

kotlin {
    jvmToolchain(17)
}
