plugins {
    alias(libs.plugins.kotlin.serialization)
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
                api(libs.kotlinx.coroutines.core)
                implementation(project(":core"))
                implementation(project(":parser-impl"))
                implementation(libs.kotlinx.serialization.core)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}
