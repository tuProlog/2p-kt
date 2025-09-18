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
                api(project(":dsl-core"))
                api(project(":unify"))
            }
        }
        commonTest {
            dependencies {
                api(project(":test-dsl"))
            }
        }
    }
}
