plugins {
    `kotlin-mp`
    `kotlin-doc`
    `publish-on-maven`
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
