kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(kotlin("reflect"))
                api(project(":core"))
                api(project(":unify"))
                api(project(":theory"))
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(project(":dsl-theory"))
            }
        }
    }
}
