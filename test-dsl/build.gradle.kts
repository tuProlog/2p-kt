kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":dsl-core"))
                api(libs.kotest.assertions.core)
                api(libs.kotest.property)
            }
        }
    }
}
