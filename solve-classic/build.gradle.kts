plugins {
    kotlin("multiplatform")
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":solve"))
                api(project(":dsl-theory"))
            }
        }

        val commonTest by getting {
            dependencies {
                api(project(":test-solve"))
            }
        }
    }
}
