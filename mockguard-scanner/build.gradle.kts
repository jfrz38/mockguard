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
