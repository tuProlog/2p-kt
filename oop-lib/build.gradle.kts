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
                api(kotlin("reflect"))
                api(project(":solve"))
            }
        }

        commonTest {
            dependencies {
                api(project(":test-solve"))
                api(project(":solve-classic"))
            }
        }
    }
}
