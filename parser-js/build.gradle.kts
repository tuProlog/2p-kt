plugins {
    id(
        libs.plugins.ktMpp.mavenPublish
            .get()
            .pluginId,
    )
}

kotlin {
    sourceSets {
        jsMain {
            dependencies {
                api(kotlin("stdlib-js"))
                api(
                    npm(
                        "@tuprolog/parser-utils",
                        libs.versions.npm.tuprolog.parserUtils
                            .get(),
                    ),
                )
            }
        }
        jsTest {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

afterEvaluate {
    publishing {
        publications.withType<MavenPublication> {
            pom.packaging = "klib"
        }
    }
}
