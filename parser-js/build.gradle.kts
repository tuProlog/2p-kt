kotlin {
    sourceSets {
        val main by getting {
            dependencies {
                api(kotlin("stdlib-js"))
                api(npm("@tuprolog/parser-utils", "0.3.0"))
            }
        }

        val test by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}
