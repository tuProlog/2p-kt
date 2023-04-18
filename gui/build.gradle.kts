plugins {
    `kotlin-mp`
    `kotlin-doc`
    `publish-on-maven`
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":parser-theory"))
                api(project(":solve"))
                api(project(":io-utils"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(project(":solve-classic"))
                implementation(project(":solve-concurrent"))
                implementation(project(":io-lib"))
            }
        }
    }
}
