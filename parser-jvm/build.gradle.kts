import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.dokka.gradle.AbstractDokkaTask
import org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask

plugins {
    antlr
    id(libs.plugins.ktMpp.mavenPublish.get().pluginId)
}

dependencies {
    antlr(libs.antlr.full)
    api(libs.antlr.runtime)

    implementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("test-junit"))
}

configurations {
    api {
        setExtendsFrom(emptyList()) // removes undesired dependency from antlr configuration
    }
}

tasks.generateGrammarSource {
    maxHeapSize = "64m"
    arguments = arguments + listOf("-visitor", "-long-messages")
    val buildDir = project.layout.buildDirectory.get().asFile
    outputDirectory = buildDir.resolve("generated-src/antlr/main/it/unibo/tuprolog/parser")
    tasks.compileKotlin.orNull?.dependsOn(this)
    tasks.findByName("sourcesJar")?.dependsOn(this)
}

tasks.generateTestGrammarSource {
    tasks.compileTestKotlin.orNull?.dependsOn(this)
}

fun dependOnGrammarGeneration(task: Task) {
    if ("Test" in task.name) {
        task.dependsOn(tasks.generateTestGrammarSource)
    } else {
        task.dependsOn(tasks.generateGrammarSource)
    }
}

tasks.withType<AbstractDokkaTask>(::dependOnGrammarGeneration)
tasks.withType<Detekt>(::dependOnGrammarGeneration)
tasks.withType<BaseKtLintCheckTask>(::dependOnGrammarGeneration)

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    filter {
        exclude("**/generated-src/**")
    }
}

tasks.withType<AbstractDokkaTask> {
    dependsOn(tasks.generateGrammarSource)
}

tasks.getByName<Jar>("sourcesJar") {
    project.sourceSets.forEach { sourceSet ->
        sourceSet.allSource.sourceDirectories.forEach {
            from(it)
        }
        sourceSet.resources.sourceDirectories.forEach {
            from(it)
        }
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}
