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
            }
        }
        val commonTest by getting {
            dependencies {
                api(project(":dsl-unify"))
            }
        }
    }
}

packageJson {
    dependencies = mutableMapOf(
        npmSubproject("unify"),
    )
}
