// Project specific kotlin multiplatform configuration
kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":solve"))
            }
        }

        val commonTest by getting {
            dependencies {
                api(project(":solve-test"))
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
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
