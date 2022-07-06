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
    }
}

packageJson {
    dependencies = mutableMapOf(
        npmSubproject("core"),
    )
}
