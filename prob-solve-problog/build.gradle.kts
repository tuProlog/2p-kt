kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":bdd"))
                api(project(":oop-lib"))
                api(project(":solve-classic"))
                api(project(":prob-solve"))
            }
        }

        jvm {
            val main = compilations["main"]
            val test = compilations["test"]

            main.defaultSourceSet {
                dependsOn(commonMain)
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
            }
            test.defaultSourceSet {
                dependsOn(main.defaultSourceSet)
            }
        }
    }
}
