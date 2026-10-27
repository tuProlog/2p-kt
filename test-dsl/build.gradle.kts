kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":dsl-core"))
                api(kotlin("test-common"))
            }
        }

        getByName("jvmMain") {
            dependencies {
                implementation(kotlin("test-junit5"))
            }
        }

        getByName("jsMain") {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}
