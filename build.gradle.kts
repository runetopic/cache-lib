import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.UsesKotlinJavaToolchain

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(deps.plugins.jvm)
}

allprojects {
    group = "com.runetopic.cache"

    apply {
        plugin("org.jetbrains.kotlin.jvm")
    }

    plugins.withType<KotlinPluginWrapper> {
        dependencies {
            implementation(kotlin("stdlib"))
        }
    }

    tasks.withType<Test> {
        dependencies {
            testImplementation(kotlin("test"))
        }
    }

    kotlin {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(JavaVersion.VERSION_11.majorVersion))
        }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(JavaVersion.VERSION_11.majorVersion))
        }
    }
}
