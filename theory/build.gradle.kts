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
            }
        }
        commonTest {
            dependencies {
                api(project(":dsl-unify"))
            }
        }
    }
}
