kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":dsl-theory"))
                api(project(":solve-classic"))
            }
        }
    }
}
