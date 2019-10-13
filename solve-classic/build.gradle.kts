// Project specific kotlin multiplatform configuration
kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":solve"))
            }
        }

        val commonTest by getting {
            dependencies {
                api(project(":solve-test"))
            }
        }

    }
}
