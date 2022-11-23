plugins {
    kotlin("jvm")
    `maven-publish`
    signing
}

version = "1.6.0"

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            pom {
                packaging = "jar"
                name.set("Xlite Cache Library")
                description.set("Cache Library for reading and writing from the jagex cache.")
                url.set("https://github.com/runetopic/cache-lib")

                developers {
                    developer {
                        id.set("tylert")
                        name.set("Tyler Telis")
                        email.set("xlitersps@gmail.com")
                    }

                    developer {
                        id.set("ultraviolet-jordan")
                        name.set("Jordan Abraham")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/runetopic/cache-lib.git")
                    developerConnection.set("scm:git:ssh://github.com/runetopic/cache-lib.git")
                    url.set("http://github.com/rune-topic/")
                }
            }

            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["java"])
        }
    }
    repositories {
        val ossrhUsername: String by project
        val ossrhPassword: String by project

        maven {
            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/releases/")
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}

dependencies {
    implementation("org.apache.commons:commons-compress:1.21")
    implementation("com.michael-bull.kotlin-inline-logger:kotlin-inline-logger:1.0.4")
    implementation("org.slf4j:slf4j-simple:2.0.4")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.0")
    implementation("com.runetopic.cryptography:cryptography:1.0.6-SNAPSHOT")
}
