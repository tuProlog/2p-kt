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
                // api(kotlin("reflect"))
                api(project(":theory"))
            }
        }
        commonTest {
            dependencies {
                implementation(project(":dsl-theory"))
            }
        }
    }
}
