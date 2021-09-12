plugins {
    kotlin("jvm")
    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            pom {
                name.set("Xlite Cache Library")
                description.set("Cache Library for reading and writing to the jagex cache in the 647 protocol.")
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
            artifactId = "cache"
            version = "1.0-SNAPSHOT"

            from(components["java"])
        }
    }
}

dependencies {
    implementation("org.apache.commons:commons-compress:1.21")
    implementation("com.michael-bull.kotlin-inline-logger:kotlin-inline-logger:1.0.3")
    implementation("org.slf4j:slf4j-simple:1.7.32")
    implementation("com.google.guava:guava-collections:r03")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.+")
}
