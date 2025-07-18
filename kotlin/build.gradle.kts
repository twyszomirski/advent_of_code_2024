plugins {
    kotlin("jvm") version "2.2.0"
}

group = "org.twyszomirski"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.assertj:assertj-core:3.27.2")
}

tasks.test {
    useJUnitPlatform()
}