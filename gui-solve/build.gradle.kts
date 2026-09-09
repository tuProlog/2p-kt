kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":gui"))
                implementation(project(":solve"))
                implementation(project(":io-lib"))
                implementation(project(":oop-lib"))
                implementation(project(":parser-impl"))
                implementation(project(":parser-theory"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
                implementation(project(":solve-classic"))
            }
        }
    }
}
