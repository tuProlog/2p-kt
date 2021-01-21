kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":prob-solve"))
                api(project(":bdd"))
            }
        }
    }
}
