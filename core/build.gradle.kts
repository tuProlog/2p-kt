kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("io.github.gciatto:kt-math:${Versions.io_github_gciatto}")
            }
        }

        // Default source set for JVM-specific sources and dependencies:
        jvm {
            compilations["main"].defaultSourceSet {
                dependencies {
//                    api("io.github.gciatto:kt-math-jvm:${Versions.io_github_gciatto}")
                }
            }
        }

        js {
            compilations["main"].defaultSourceSet {
                dependencies {
//                    api("io.github.gciatto:kt-math-js:${Versions.io_github_gciatto}")
                }
            }
        }
    }
}
