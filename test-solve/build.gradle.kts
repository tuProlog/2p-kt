plugins {
    id(
        libs.plugins.ktMpp.mavenPublish
            .get()
            .pluginId,
    )
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":solve"))
                api(project(":dsl-theory"))
                api(kotlin("test-common"))
                api(kotlin("test-annotations-common"))
            }
        }

        getByName("jvmMain") {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        getByName("jsMain") {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}
