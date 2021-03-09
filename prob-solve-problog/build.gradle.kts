kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":bdd"))
                api(project(":solve-classic"))
                api(project(":prob-solve"))
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
