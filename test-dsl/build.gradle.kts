kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":dsl-core"))
                api(kotlin("test-common"))
                api(kotlin("test-annotations-common"))
            }
        }

        getByName("jvmMain") {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        getByName("jsMain") {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}
