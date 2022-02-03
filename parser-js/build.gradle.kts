plugins {
    `kotlin-js-only`
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
