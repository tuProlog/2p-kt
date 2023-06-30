plugins {
    id(libs.plugins.ktMpp.mavenPublish.get().pluginId)
}

kotlin {
    sourceSets {
        val main by getting {
            dependencies {
                api(kotlin("stdlib-js"))
                api(npm("@tuprolog/parser-utils", libs.versions.npm.tuprolog.parserUtils.get()))
            }
        }

        val test by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}
