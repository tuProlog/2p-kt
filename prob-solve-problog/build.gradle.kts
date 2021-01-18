kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":bdd"))
                api(project(":solve-classic"))
                api(project(":prob-solve"))
            }
        }

        val commonTest by getting {
            dependencies {
                api(project(":parser-theory"))
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
                dependsOn(commonTest)
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
                dependsOn(commonTest)
            }
        }
    }
}
