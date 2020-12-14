kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":solve"))
                api(project(":parser-theory"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:_")
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(project(":test-solve"))
                implementation(project(":solve-classic"))
                implementation(project(":solve-streams"))
            }
        }

        val jsMain by getting {
            dependencies {
                implementation(npm("sync-request", "6.1.0"))
            }
        }
    }
}
