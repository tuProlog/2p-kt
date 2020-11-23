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
val ktFreeCompilerArgsJvm: String by project

dependencies {
    antlr("org.antlr:antlr4:_")
    api("org.antlr:antlr4-runtime:_")

    implementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("test-junit"))
}

configure<JavaPluginConvention> {
    targetCompatibility = JavaVersion.valueOf("VERSION_1_$javaVersion")
    sourceCompatibility = JavaVersion.valueOf("VERSION_1_$javaVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.$javaVersion"
        freeCompilerArgs = ktFreeCompilerArgsJvm.split(";").toList()
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

tasks.getByName<Jar>("sourcesJar") {
    project.sourceSets.forEach { sourceSet ->
        sourceSet.allSource.sourceDirectories.forEach {
            from(it)
        }
        sourceSet.resources.sourceDirectories.forEach {
            from(it)
        }
    }
}
