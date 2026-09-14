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
                // api, not implementation: SolveOptions (from :solve) appears in SolverFactorySession's
                // protected configureSolveOptions(...), so subclassing modules (e.g. gui-plp) need it visible.
                api(project(":solve"))
                implementation(project(":core"))
                implementation(project(":parser-impl"))
                implementation(project(":parser-theory"))
                implementation(project(":io-lib"))
                implementation(project(":oop-lib"))
                implementation(libs.kotlinx.serialization.core)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
                implementation(project(":solve-classic"))
            }
        }
    }
}
