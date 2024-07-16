plugins {
    kotlin("jvm") version "2.0.0"
    java
    id("org.jetbrains.dokka") version "1.9.20"
}

group = "io.github.createduser"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
}
kotlin {
    jvmToolchain(8)
}