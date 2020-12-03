plugins {
    kotlin("multiplatform")
}

kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":solve"))
                api(project(":dsl-theory"))
                api(kotlin("test-common"))
                api(kotlin("test-annotations-common"))
            }
        }

        jvm {
            val main = compilations["main"]
            val test = compilations["test"]

            main.defaultSourceSet {
                dependencies {
                    implementation(kotlin("test-junit"))
                }
            }
        }

        js {
            val main = compilations["main"]
            val test = compilations["test"]

            main.defaultSourceSet {
                dependencies {
                    implementation(kotlin("test-js"))
                }
            }
        }
    }
}
