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
                implementation(project(":parser-impl"))
            }
        }

        commonTest {
            dependencies {
                api(project(":dsl-theory"))
            }
        }
    }
}
