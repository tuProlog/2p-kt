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
                api(project(":dsl-unify"))
                api(project(":theory"))
            }
        }
        commonTest {
            dependencies {
                api(project(":test-dsl"))
            }
        }
    }
}
