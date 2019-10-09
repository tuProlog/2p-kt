// Project specific kotlin multiplatform configuration
kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":solve"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:1.3.0-RC2")
            }
        }

        // Default source set for JVM-specific sources and dependencies:
        jvm {
            compilations["main"].defaultSourceSet {
                dependencies {
//                    api(project(":core"))
//                    api(project(":unify"))
//                    api(project(":theory"))

                    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.0-RC2")
                }
            }
        }

        js {
            compilations["main"].defaultSourceSet {
                dependencies {
//                    api(project(":core"))
//                    api(project(":unify"))
//                    api(project(":theory"))

                    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.3.0-RC2")
                }
            }
        }
    }
}
