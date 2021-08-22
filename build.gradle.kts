plugins {
    kotlin("jvm") version "1.5.21"
}

group = "com.xlite.cache"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.apache.commons:commons-compress:1.21")
    implementation("com.michael-bull.kotlin-inline-logger:kotlin-inline-logger:1.0.3")
    implementation("io.insert-koin:koin-core:3.1.2")
    implementation("org.slf4j:slf4j-simple:1.7.32")
}