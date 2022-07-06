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
                api(project(":solve"))
                api(project(":bdd"))
            }
        }
    }
}

packageJson {
    dependencies = mutableMapOf(
        npmSubproject("solve"),
        npmSubproject("bdd"),
    )
}
