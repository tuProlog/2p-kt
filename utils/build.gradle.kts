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
                api(libs.ktMath)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotest.property)
            }
        }
    }
}
