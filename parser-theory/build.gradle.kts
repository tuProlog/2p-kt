plugins {
    kotlin("multiplatform")
}

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

        val commonTest by getting {
            dependencies {
                api(project(":dsl-theory"))
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
