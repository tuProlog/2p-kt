import org.jetbrains.kotlin.konan.util.visibleName

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
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
    implementation(libs.kotlin.bom)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.ktNpmPublish)
    implementation(libs.dokka)
    implementation(libs.ktlint)
    implementation(libs.detekt)
    implementation(libs.javalin)
}

gradlePlugin {
    plugins {
        create("mock-service") {
            id = "mock-service"
            displayName = "Mock Service"
            description = "Mock Service Plugin for starting WS in Gradle"
            implementationClass = "MockServicePlugin"
        }
    }
}
