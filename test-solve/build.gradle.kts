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
                api(libs.kotest.framework.engine)
                api(libs.kotest.assertions.core)
            }
        }

        getByName("jvmMain") {
            dependencies {
                // JUnit Jupiter, for the Java contract (JavaConstructionContract): Java tests can't be Kotest specs
                implementation(kotlin("test-junit5"))
            }
        }
    }
}
