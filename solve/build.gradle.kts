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
                api(project(":core"))
                api(project(":unify"))
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
