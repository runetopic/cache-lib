plugins {
    kotlin("jvm")
    `maven-publish`
    signing
}

version = "647.4.0-SNAPSHOT"

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            pom {
                name.set("Xlite Cache Library Type Loaders")
                description.set("Definition types of runescape files.")
                url.set("https://github.com/xlite2/cache-lib")

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


                artifact(tasks["javadocJar"])
                artifact(tasks["sourcesJar"])
            }
        }
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["java"])
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
}

signing {
    sign(publishing.publications["mavenJava"])
}

dependencies {
    implementation(project(":cache"))
}
