rootProject.name = "cache-lib"

pluginManagement {
    plugins {
        kotlin("jvm") version "1.5.21"
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}


include("app")
include("cache")
include("loader")
