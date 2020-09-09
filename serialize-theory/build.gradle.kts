kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":theory"))
                api(project(":serialize-core"))
            }
        }

        val commonTest by getting {
            dependencies {
                api(project(":solve"))
            }
        }

        jvm {
            compilations["main"].defaultSourceSet {
                dependencies {
                    implementation(Libs.jackson_core)
                    implementation(Libs.jackson_datatype_jsr310)
                    implementation(Libs.jackson_dataformat_yaml)
                    implementation(Libs.jackson_dataformat_xml)
                }
            }
        }

        js {
            compilations["main"].defaultSourceSet {
                dependencies {
//                    api(npm("yaml", "^1.10.0"))
                }
            }
        }
    }
}

listOf("yaml", "json").forEach {
    tasks.create("print${it.capitalize()}", JavaExec::class.java) {
        group = "application"
        dependsOn("jvmTestClasses")
        classpath = files(
            kotlin.jvm().compilations.getByName("test").output,
            kotlin.jvm().compilations.getByName("test").compileDependencyFiles
        )
        standardInput = System.`in`
        main = "${it.toUpperCase()}PrinterKt"
    }
}

