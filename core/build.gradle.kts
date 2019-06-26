val ktMathVersion: String by project

// Project specific kotlin multiplatform configuration
kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("io.github.gciatto:kt-math-metadata:$ktMathVersion")
            }
        }

        // Default source set for JVM-specific sources and dependencies:
        jvm {
            compilations["main"].defaultSourceSet {
                dependencies {
                    api("io.github.gciatto:kt-math-jvm:$ktMathVersion")
                }
            }
        }

        js {
            compilations["main"].defaultSourceSet {
                dependencies {
                    api("io.github.gciatto:kt-math-js:$ktMathVersion")
                }
            }
        }
    }
}
