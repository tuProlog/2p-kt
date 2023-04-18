plugins {
    `kotlin-js-only`
    `kotlin-doc`
    `publish-on-maven`
}

kotlin {
    sourceSets {
        val main by getting {
            dependencies {
                api(kotlin("stdlib-js"))
                api(project(":solve-classic"))
                api(project(":parser-theory"))

                // implementation(enforcedPlatform("org.jetbrains.kotlin-wrappers:kotlin-wrappers-bom:1.0.0-pre.527"))
                // implementation("org.jetbrains.kotlin-wrappers:kotlin-react")
                // implementation("org.jetbrains.kotlin-wrappers:kotlin-react-dom")
                // implementation("org.jetbrains.kotlin-wrappers:kotlin-emotion")
                // implementation("org.jetbrains.kotlin-wrappers:kotlin-mui")
                // implementation("org.jetbrains.kotlin-wrappers:kotlin-mui-icons")
                //
                // implementation(npm("date-fns", "2.29.3"))
                // implementation(npm("@date-io/date-fns", "2.16.0"))
                // implementation(npm("@monaco-editor/react", "4.4.6"))
            }
        }
        val test by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
    js {
        browser()
        binaries.executable()
    }
}
