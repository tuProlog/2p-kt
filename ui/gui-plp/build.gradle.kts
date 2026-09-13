plugins {
    // id(...pluginId), not alias(...): see full/build.gradle.kts's plugins block for why.
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
                api(project(":gui"))
                implementation(project(":bdd"))
                implementation(project(":solve-plp"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(project(":solve-problog"))
                implementation(project(":parser-theory"))
            }
        }
    }
}
