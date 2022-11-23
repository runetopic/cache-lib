plugins {
    kotlin("jvm")
    `maven-publish`
    signing
}

version = "647.6.4-SNAPSHOT"

java {
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    implementation(project(":cache"))
}
