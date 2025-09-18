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
                api(project(":dsl-theory"))
                api(project(":solve"))
            }
        }
        commonTest {
            dependencies {
                api(project(":solve-classic"))
            }
        }
    }
}
