import org.jetbrains.kotlin.gradle.plugin.KotlinDependencyHandler

plugins {
    `kotlin-js-only`
    `kotlin-doc`
    `publish-on-maven`
}

fun kotlinw(target: String): String =
    "org.jetbrains.kotlin-wrappers:kotlin-$target"

@Suppress("NAME_SHADOWING")
fun KotlinDependencyHandler.bom(version: Provider<MinimalExternalModuleDependency>) {
    val module = version.map { it.module }.get()
    val version = version.map { it.versionConstraint.requiredVersion }.get()
    implementation(enforcedPlatform("$module:$version"))
}

kotlin {
    sourceSets {
        val main by getting {
            dependencies {
                api(project(":solve"))
                api(project(":gui"))
                api(project(":io-lib"))
                api(project(":oop-lib"))
                api(project(":parser-theory"))
                api(project(":solve-classic"))

                bom(libs.kotlin.wrappers.bom)

                implementation(kotlinw("react"))
                implementation(kotlinw("react-dom"))
                implementation(kotlinw("emotion"))
                implementation(kotlinw("mui"))
                implementation(kotlinw("mui-icons"))
                // TODO: selezionare solo il redux che ci serve e non entrambi
                implementation(kotlinw("react-redux"))
                implementation(kotlinw("redux"))

                implementation(npm("date-fns", "2.29.3"))
                implementation(npm("@date-io/date-fns", "2.16.0"))
                implementation(npm("@monaco-editor/react", "4.4.6"))
            }


            tasks.getByPath("detekt").onlyIf {
                gradle.startParameter.taskNames.any { it.contains("detekt") }
            }

//            tasks.getByPath("ktlintMainSourceSetCheck").onlyIf {
//                gradle.startParameter.taskNames.any { it.contains("ktlintMainSourceSetCheck") }
//            }
//            tasks.getByPath("ktlintKotlinScriptCheck").onlyIf {
//                gradle.startParameter.taskNames.any { it.contains("ktlintKotlinScriptCheck") }
//            }
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
