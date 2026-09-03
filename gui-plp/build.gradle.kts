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
            }
        }
    }
}
