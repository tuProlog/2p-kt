kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":gui"))
                implementation(project(":bdd"))
                implementation(project(":solve-plp"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(project(":solve-problog"))
                implementation(project(":parser-theory"))
            }
        }
    }
}
