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

        val commonTest by getting {
            dependsOn(commonMain)
        }

        jvm {
            val main = compilations["main"]
            val test = compilations["test"]

            main.defaultSourceSet {
                dependsOn(commonMain)
                dependencies {
                    implementation(kotlin("test-junit"))
                }
            }
            test.defaultSourceSet {
                dependsOn(main.defaultSourceSet)
            }
        }

        js {
            val main = compilations["main"]
            val test = compilations["test"]

            main.defaultSourceSet {
                dependsOn(commonMain)
                dependencies {
                    implementation(kotlin("test-js"))
                }
            }
            test.defaultSourceSet {
                dependsOn(main.defaultSourceSet)
            }
        }
    }
}
