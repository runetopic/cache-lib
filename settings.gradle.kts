rootProject.name = "cache-lib"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven {
            url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        }
    }

    versionCatalogs {
        create("deps") {
            version("kotlin", "1.7.10")
            plugin("jvm", "org.jetbrains.kotlin.jvm").versionRef("kotlin")
        }
    }
}

listOf(
    "app",
    "cache",
    "loader"
).also(::include)
