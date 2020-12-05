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
