kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":dsl-core"))
                api(project(":unify"))
            }
        }
    }
}
