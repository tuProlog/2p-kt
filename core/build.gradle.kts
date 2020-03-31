import org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinPackageJsonTask

kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api("io.github.gciatto:kt-math:${Versions.kt_math}")
            }
        }
    }
}

//tasks.getByName<KotlinPackageJsonTask>("jsPackageJson") {
//    println(this::class.java)
//    this.
//}