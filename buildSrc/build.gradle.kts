import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    // Rasterizes the default logo SVG to PNG in-process (see IdeLogo.kt) instead of shelling out to inkscape.
    // batik-codec registers the PNG `WriteAdapter` that PNGTranscoder needs; transcoder alone doesn't have one.
    implementation(libs.batik.transcoder)
    implementation(libs.batik.codec)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.jackson.core)
    implementation(libs.jackson.databind)
}

kotlin {
    target {
        compilerOptions {
            allWarningsAsErrors = true
            languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_4)
        }
    }
}
