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
                api(project(":dsl-theory"))
                api(project(":solve"))
            }
        }
        val commonTest by getting {
            dependencies {
                api(project(":solve-classic"))
            }
        }
    }
}

packageJson {
    dependencies = mutableMapOf(
        npmSubproject("dsl-theory"),
        npmSubproject("solve"),
    )
}
