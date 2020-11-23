kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":core"))
            }
        }

        jvm {
            compilations["main"].defaultSourceSet {
                dependencies {
                    implementation("com.fasterxml.jackson.core:jackson-core:_")
                    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:_")
                    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:_")
                    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:_")
                }
            }
        }

        js {
            compilations["main"].defaultSourceSet {
                dependencies {
                    api(npm("yaml", "^1.10.0"))
                }
            }
        }
    }
}
