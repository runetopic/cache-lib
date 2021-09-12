plugins {
    kotlin("jvm")
    `maven-publish`
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
            }
        }
        create<MavenPublication>("maven") {
            groupId = "com.xlite.cache"
            artifactId = "loader"
            version = "647.0-SNAPSHOT"

            from(components["java"])
        }
    }
}

dependencies {
    implementation(project(":cache"))
}
