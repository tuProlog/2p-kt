kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("io.github.gciatto:kt-math:${Versions.kt_math}")
            }
        }

        // Default source set for JVM-specific sources and dependencies:
        jvm {
            compilations["main"].defaultSourceSet {
                dependencies {
//                    api("io.github.gciatto:kt-math-jvm:${Versions.kt_math}")
                }
            }
        }

        js {
            compilations["main"].defaultSourceSet {
                dependencies {
//                    api("io.github.gciatto:kt-math-js:${Versions.kt_math}")
                }
            }
        }
    }
}
