plugins {
    `kotlin-mp`
    `kotlin-doc`
    `publish-on-maven`
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":dsl-unify"))
                api(project(":theory"))
            }
        }
    }
}

packageJson {
    dependencies = mutableMapOf(
        npmSubproject("dsl-unify"),
        npmSubproject("theory"),
    )
}
