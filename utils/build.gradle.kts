kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(libs.ktMath)
            }
        }
    }
}
