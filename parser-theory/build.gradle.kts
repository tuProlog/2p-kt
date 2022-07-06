plugins {
    `kotlin-mp`
    `kotlin-doc`
    `publish-on-maven`
    `publish-on-npm`
}

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

packageJson {
    dependencies = mutableMapOf(
        npmSubproject("theory"),
        npmSubproject("parser-core"),
    )
}
