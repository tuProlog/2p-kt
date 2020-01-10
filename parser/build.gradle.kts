// Project specific kotlin multiplatform configuration
kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":core"))
            }
        }

        // Default source set for JVM-specific sources and dependencies:
        jvm {
            compilations["main"].defaultSourceSet {
                dependencies {
                    api(project(":parser-jvm"))
                }
            }
        }

        js {
            compilations["main"].defaultSourceSet {
                dependencies {
                }
            }
        }
    }
}
