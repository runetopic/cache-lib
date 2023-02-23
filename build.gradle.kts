import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.tasks.UsesKotlinJavaToolchain

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(deps.plugins.jvm)
}

allprojects {
    group = "com.runetopic.cache"

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

    tasks.withType<KotlinCompile> {
        kotlin {
            jvmToolchain {
                languageVersion.set(JavaLanguageVersion.of(18))
            }
        }
    }

    tasks.withType<UsesKotlinJavaToolchain>().configureEach {
        kotlinJavaToolchain.toolchain.use(
            project.extensions.getByType<JavaToolchainService>().launcherFor {
                languageVersion.set(JavaLanguageVersion.of(18))
            }
        )
    }
}