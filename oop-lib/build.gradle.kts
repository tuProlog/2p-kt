plugins {
    `kotlin-jvm-js`
    `kotlin-doc`
    `publish-on-maven`
    `publish-on-npm`
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(kotlin("reflect"))
                api(project(":solve"))
            }
        }

        val commonTest by getting {
            dependencies {
                api(project(":test-solve"))
                api(project(":solve-classic"))
            }
        }
    }
}
