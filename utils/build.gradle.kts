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
                api(libs.ktMath)
            }
        }
    }
}

packageJson {
    dependencies = mutableMapOf(
        "kotlin" to libs.kotlin.stdlib.js.version,
        "kt-math" to libs.ktMath.version,
    )
}
