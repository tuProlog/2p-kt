plugins {
    id(libs.plugins.ktMpp.mavenPublish.get().pluginId)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":core"))
            }
        }
//        commonTest {
//            dependencies {
//                api(project(":test-dsl"))
//            }
//        }
    }
}
