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
                api(project(":bdd"))
                api(project(":solve-classic"))
                api(project(":solve-plp"))
            }
        }

        commonTest {
            dependencies {
                api(project(":parser-theory"))
                api(project(":test-solve"))
            }
        }
    }
}
