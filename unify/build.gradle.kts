// Project specific kotlin multiplatform configuration
kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
//                api("com.github.gciatto:kt-math-metadata:0.+")
                api(project(":core"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(project(":dsl"))
            }
        }

        // Default source set for JVM-specific sources and dependencies:
        jvm {
            compilations["main"].defaultSourceSet {
                dependencies {
                    api(project(":core"))
                }
            }
        }

        js {
            compilations["main"].defaultSourceSet {
                dependencies {
                    api(project(":core"))
                }
            }
        }
    }
}
