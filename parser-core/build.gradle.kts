plugins {
    kotlin("multiplatform")
}

kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":core"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(project(":test-solve"))
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
                    api(project(":parser-js"))
                }
            }
        }
    }
}
