plugins {
    `kotlin-dsl`
    alias(libs.plugins.kotlin.jvm)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(libs.shadowJar)
    implementation(libs.kotlin.gradlePlugin)
}

val targetJvm = libs.versions.jvm.get()
val targetJava = JavaVersion.valueOf("VERSION_$targetJvm")

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = targetJvm
    }
}

java {
    sourceCompatibility = targetJava
    targetCompatibility = targetJava
}
