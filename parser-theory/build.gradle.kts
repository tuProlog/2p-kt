// Project specific kotlin multiplatform configuration
kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":core"))
                api(project(":unify"))
                api(project(":theory"))
                api(project(":parser-core"))
            }
        }

        jvm {
            compilations["main"].defaultSourceSet {
                dependencies {
                    api(project(":parser-jvm"))
                }
            }
        }

        js {
            compilations["main"].defaultSourceSet {
                dependencies {
                    api(project(":parser-js"))
                }
            }
        }
    }
}
