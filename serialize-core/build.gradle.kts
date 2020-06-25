kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":core"))
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
                    npm("yaml", "1.10.0")
                }
            }
        }
    }
}
