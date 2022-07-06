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
                api(project(":bdd"))
                api(project(":solve-classic"))
                api(project(":solve-plp"))
            }
        }

        val commonTest by getting {
            dependencies {
                api(project(":parser-theory"))
                api(project(":test-solve"))
            }
        }
    }
}

packageJson {
    dependencies = mutableMapOf(
        npmSubproject("solve-classic"),
        npmSubproject("solve-plp"),
    )
}
