// Project specific kotlin multiplatform configuration
kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":core"))
                api(project(":unify"))
                api(project(":theory"))
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
//                    api(project(":core"))
//                    api(project(":unify"))
//                    api(project(":theory"))
                }
            }
        }

        js {
            compilations["main"].defaultSourceSet {
                dependencies {
//                    api(project(":core"))
//                    api(project(":unify"))
//                    api(project(":theory"))
                }
            }
        }
    }
}
