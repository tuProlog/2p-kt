// Project specific kotlin multiplatform configuration
kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":solve"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.3.0-RC2")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(project(":solve-test"))
            }
        }

        // Default source set for JVM-specific sources and dependencies:
        jvm {
            compilations["main"].defaultSourceSet {
                dependencies {
                    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.0-RC2")
                }
            }

        }

        js {
            compilations["main"].defaultSourceSet {
                dependencies {
                    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.3.0-RC2")
                }
            }
        }
    }
}
