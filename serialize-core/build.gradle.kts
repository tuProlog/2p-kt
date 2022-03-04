plugins {
    `kotlin-mp`
    `kotlin-doc`
    `publish-on-maven`
    `publish-on-npm`
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":core"))
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(libs.jackson.core)
                implementation(libs.jackson.xml)
                implementation(libs.jackson.yaml)
                implementation(libs.jackson.jsr310)
            }
        }

        val jsMain by getting {
            dependencies {
                api(npm("yaml", libs.versions.npm.yaml.get()))
            }
        }
    }
}
