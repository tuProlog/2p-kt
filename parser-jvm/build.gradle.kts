import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("maven-publish")
    signing
    id("org.jetbrains.dokka")
    id("com.jfrog.bintray")
    id("java-library")
    antlr
}

val javaVersion: String by project
val ktFreeCompilerArgs: String by project

dependencies {
    antlr("org.antlr", "antlr4", Versions.org_antlr)

    api("org.antlr", "antlr4-runtime", Versions.org_antlr)

    implementation(kotlin("stdlib-jdk8"))

//    testImplementation("pl.pragmatists:JUnitParams:1.1.1")
//    testImplementation("junit", "junit", "4.13")
    testImplementation(kotlin("test-junit"))
}


configure<JavaPluginConvention> {
    targetCompatibility = JavaVersion.valueOf("VERSION_1_$javaVersion")
    sourceCompatibility = JavaVersion.valueOf("VERSION_1_$javaVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.$javaVersion"
        freeCompilerArgs = ktFreeCompilerArgs.split(";").toList()
    }
}

configurations {
    compile {
        setExtendsFrom(emptyList())
    }
}

tasks.generateGrammarSource {
    maxHeapSize = "64m"
    arguments = arguments + listOf("-visitor", "-long-messages")
    outputDirectory = File("${project.buildDir}/generated-src/antlr/main/it/unibo/tuprolog/parser")
}

tasks.create("jvmTest") {
    dependsOn("test")
}
