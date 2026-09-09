kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.kotlinx.coroutines.core)
                implementation(project(":core"))
                implementation(project(":parser-impl"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}
