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

packageJson {
    dependencies = mutableMapOf(
        npmSubproject("core"),
        npmSubproject("parser-js"),
    )
}
