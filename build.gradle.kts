
// See note below, this block serves the legacy plugin application
buildscript {

    // plugin repositories
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }

    // plugin dependencies
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.40")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:0.9.18")
//        classpath("com.moowork.gradle:gradle-node-plugin:1.3.1")
    }
}

// apply next commands to all subprojects
subprojects {

    group = "it.unibo.tuprolog"
    version = "1.0-SNAPSHOT"

    // ** NOTE ** legacy plugin application, because the new "plugins" block is not available inside "subprojects" scope yet
    apply(plugin = "org.jetbrains.kotlin.multiplatform")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "org.jetbrains.dokka")
//    apply(plugin = "com.moowork.node")

    // projects dependencies repositories
    repositories {
        mavenCentral()
        maven("https://dl.bintray.com/kotlin/dokka")
        maven("https://jitpack.io")
        mavenLocal()
    }
}
