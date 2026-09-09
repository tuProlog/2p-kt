kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":gui"))
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
