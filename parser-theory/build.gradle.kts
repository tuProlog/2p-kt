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
                api(project(":unify"))
                api(project(":theory"))
                api(project(":parser-core"))
            }
        }

        commonTest {
            dependencies {
                api(project(":dsl-theory"))
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
