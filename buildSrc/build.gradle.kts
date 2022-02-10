plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.jvm)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

//val javaVersion = JavaVersion.current()
//
//java {
//    targetCompatibility = javaVersion
//    sourceCompatibility = javaVersion
//}
//
//tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
//    kotlinOptions {
//        allWarningsAsErrors = false
//        jvmTarget = javaVersion.toString()
//        languageVersion = "1.6"
//    }
//}

dependencies {
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.npmPublish)
    implementation(libs.dokka)
}
