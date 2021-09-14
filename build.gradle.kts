import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

plugins {
    kotlin("jvm")
}

configure(allprojects) {
    group = "com.runetopic.cache"

    plugins.withType<KotlinPluginWrapper> {
        java.sourceCompatibility = JavaVersion.VERSION_16
        java.targetCompatibility = JavaVersion.VERSION_16

        tasks {
            compileKotlin {
                kotlinOptions.jvmTarget = "1.8"
            }
            compileTestKotlin {
                kotlinOptions.jvmTarget = "1.8"
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
