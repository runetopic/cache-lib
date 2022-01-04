rootProject.name = "cache-lib"

pluginManagement {
    plugins {
        kotlin("jvm") version "1.6.10"
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        }
    }
}

include("app")
include("cache")
include("loader")
