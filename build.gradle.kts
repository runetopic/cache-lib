import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

plugins {
    kotlin("jvm")
}

configure(allprojects) {
    group = "com.runetopic.cache"

    plugins.withType<KotlinPluginWrapper> {
        java.sourceCompatibility = JavaVersion.VERSION_17
        java.targetCompatibility = JavaVersion.VERSION_17

        tasks {
            compileKotlin {
                kotlinOptions.jvmTarget = "1.8"
                kotlinOptions.freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
            }
            compileTestKotlin {
                kotlinOptions.jvmTarget = "1.8"
                kotlinOptions.freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
            }
        }
    }
}

configure(subprojects) {
    plugins.withType<KotlinPluginWrapper> {
        dependencies {
            implementation(kotlin("stdlib"))
        }
    }
}
