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
            dependsOn(commonMain)
//            dependencies {
//                implementation(project(":solve-test"))
//            }
        }

        // Default source set for JVM-specific sources and dependencies:
        jvm {
            val main = compilations["main"]
            val test = compilations["test"]

            main.defaultSourceSet {
                dependsOn(commonMain)
                dependencies {

                }
            }
            test.defaultSourceSet {
                dependsOn(main.defaultSourceSet)
            }
        }
//
        js {

            val main = compilations["main"]
            val test = compilations["test"]

            main.defaultSourceSet {
                dependsOn(commonMain)
                dependencies {
                    //                    api(project(":core"))
//                    api(project(":unify"))
//                    api(project(":theory"))
                }
            }
            test.defaultSourceSet {
                dependsOn(main.defaultSourceSet)
            }
        }
    }
}
