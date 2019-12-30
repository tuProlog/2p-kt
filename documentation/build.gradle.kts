plugins {
    id("com.eden.orchidPlugin") version "0.18.1"
}

dependencies {
    orchidRuntimeOnly("io.github.javaeden.orchid:OrchidDocs:0.18.1")
    orchidRuntimeOnly("io.github.javaeden.orchid:OrchidKotlindoc:0.18.1")
    orchidRuntimeOnly("io.github.javaeden.orchid:OrchidPluginDocs:0.18.1")
}

repositories {
    jcenter()
    maven("https://kotlin.bintray.com/kotlinx/")
}

orchid {
    theme = "Editorial"
//    baseUrl = "http://username.github.io/project"
    version = "1.0.0"
}