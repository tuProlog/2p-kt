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

dependencies {
    implementation(libs.kotlin.bom)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.npmPublish)
    implementation(libs.dokka)
    implementation(libs.ktlint)
    implementation(libs.detekt)
    implementation(libs.javalin)
    implementation(libs.shadowJar)
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
