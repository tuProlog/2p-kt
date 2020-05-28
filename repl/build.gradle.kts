kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(Libs.clikt_multiplatform)
                api(project(":core"))
                api(project(":solve-classic"))
                api(project(":parser-theory"))
            }
        }
    }
}
