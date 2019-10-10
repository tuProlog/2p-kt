val ktVersion: String? by rootProject

// Project specific kotlin multiplatform configuration
kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":solve"))
                api(project(":dsl-theory"))
                implementation("org.jetbrains.kotlin:kotlin-test-common:$ktVersion")
                implementation("org.jetbrains.kotlin:test-annotations-common:$ktVersion")
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
