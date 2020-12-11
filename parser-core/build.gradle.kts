kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":core"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(project(":test-solve"))
            }
        }

        val jvmMain by getting {
            dependencies {
                api(project(":parser-jvm"))
            }
        }

        val jsMain by getting {
            dependencies {
                api(project(":parser-js"))
            }
        }
    }
}
