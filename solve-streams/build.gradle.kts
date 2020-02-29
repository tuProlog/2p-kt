val javaVersion: String by project
val ktFreeCompilerArgsJvm: String by project

kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":solve"))
                api(project(":dsl-theory"))
            }
        }

        val commonTest by getting {
            dependsOn(commonMain)
            dependencies {
                api(project(":solve-test"))
            }
        }

        jvm {
            val main = compilations["main"]
            val test = compilations["test"]

            main.defaultSourceSet {
                dependsOn(commonMain)
            }
            test.defaultSourceSet {
                dependsOn(commonTest)
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
                dependsOn(commonTest)
                dependsOn(main.defaultSourceSet)
            }
        }

    }
}

//tasks.withType<KotlinJvmTest> {
//    jvmArgs("-Xmx1G", "-Xss256M")
//    println("${name}.jvmArgs: $jvmArgs")
//    println("${name}.allJvmArgs: $allJvmArgs")
//}