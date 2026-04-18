plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "2.0.20"
}

group = "com.mockguard"
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
                url.set("https://github.com/mockguard/mockguard")
                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
            }
        }
    }
}

kotlin {
    jvmToolchain(17)
}
