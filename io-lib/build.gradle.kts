kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":solve"))
                api(project(":parser-theory"))
            }
        }

        js {
            compilations["main"].defaultSourceSet {
                dependencies {
                    api(npm("node-fetch", "^2.6.1"))
                }
            }
        }
    }
}
