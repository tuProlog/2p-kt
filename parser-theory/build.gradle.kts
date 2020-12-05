kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":core"))
                api(project(":unify"))
                api(project(":theory"))
                api(project(":parser-core"))
            }
        }

        val commonTest by getting {
            dependencies {
                api(project(":dsl-theory"))
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
