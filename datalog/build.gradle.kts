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
                // api(kotlin("reflect"))
                api(project(":theory"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(project(":dsl-theory"))
            }
        }
    }
}

packageJson {
    dependencies = mutableMapOf(
        npmSubproject("theory"),
    )
}
