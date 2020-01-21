// Project specific kotlin multiplatform configuration
kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":solve"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${Versions.org_jetbrains_kotlinx_kotlinx_coroutines}")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(project(":solve-test"))
            }
        }

        // Default source set for JVM-specific sources and dependencies", "
        jvm {
            compilations["main"].defaultSourceSet {
                dependencies {
                    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.org_jetbrains_kotlinx_kotlinx_coroutines}")
                }
            }

        }

        js {
            compilations["main"].defaultSourceSet {
                dependencies {
                    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:${Versions.org_jetbrains_kotlinx_kotlinx_coroutines}")
                }
            }
        }
    }
}
