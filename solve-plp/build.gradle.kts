plugins {
    `kotlin-mp`
    `kotlin-doc`
    `publish-on-maven`
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":solve"))
                api(project(":bdd"))
            }
        }
    }
}
