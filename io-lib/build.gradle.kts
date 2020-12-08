kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":solve"))
                api(project(":parser-theory"))
            }
        }

        val jsMain by getting {
            dependencies {
                api(npm("node-fetch", "^2.6.1"))
            }
        }
    }
}
