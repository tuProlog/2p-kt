plugins {
    id(libs.plugins.ktMpp.mavenPublish.get().pluginId)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":core"))
                api(project(":unify"))
            }
        }
        val commonTest by getting {
            dependencies {
                api(project(":dsl-unify"))
            }
        }
    }
}
