plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

val javaVersion = JavaVersion.current()

java {
    targetCompatibility = javaVersion
    sourceCompatibility = javaVersion
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        allWarningsAsErrors = true
        jvmTarget = javaVersion.toString()
    }
}

dependencies {
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.npmPublish)
}
