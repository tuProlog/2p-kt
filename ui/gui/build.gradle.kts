plugins {
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktMpp.mavenPublish)
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
