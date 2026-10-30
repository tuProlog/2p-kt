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
                api(project(":parser-core"))
                api(kotlin("test-common"))
                api(libs.kotest.framework.engine)
                api(libs.kotest.assertions.core)
            }
        }

        getByName("jvmMain") {
            dependencies {
                implementation(kotlin("test-junit5"))
            }
        }

        getByName("jsMain") {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}
