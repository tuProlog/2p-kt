plugins {
    id(
        libs.plugins.ktMpp.mavenPublish
            .get()
            .pluginId,
    )
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":core"))
            }
        }

        getByName("jvmMain") {
            dependencies {
                implementation(libs.jackson.core)
                implementation(libs.jackson.xml)
                implementation(libs.jackson.yaml)
                implementation(libs.jackson.jsr310)
            }
        }

        getByName("jsMain") {
            dependencies {
                api(
                    npm(
                        "yaml",
                        libs.versions.npm.yaml
                            .get(),
                    ),
                )
            }
        }
    }
}
