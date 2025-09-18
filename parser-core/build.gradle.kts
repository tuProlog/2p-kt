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

        commonTest {
            dependencies {
                implementation(project(":test-solve"))
            }
        }

        getByName("jvmMain") {
            dependencies {
                api(project(":parser-jvm"))
            }
        }

        getByName("jsMain") {
            dependencies {
                api(project(":parser-js"))
            }
        }
    }
}
