kotlin {

    sourceSets {
        val commonMain by getting {
            dependencies {
                api(Libs.clikt)
            }
        }
    }
}
