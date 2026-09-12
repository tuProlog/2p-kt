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
    implementation("org.apache.xmlgraphics:batik-transcoder:1.19")
    implementation("org.apache.xmlgraphics:batik-codec:1.19")
}
