plugins {
    kotlin("multiplatform")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":core"))
                api(project(":unify"))
                api(project(":theory"))
            }
        }
    }
}
