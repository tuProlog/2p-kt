import org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinPackageJsonTask

kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(Libs.kt_math)
            }
        }
    }
}