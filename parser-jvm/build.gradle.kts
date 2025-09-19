import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.dokka.gradle.AbstractDokkaTask
import org.jlleitschuh.gradle.ktlint.tasks.BaseKtLintCheckTask

plugins {
    antlr
    id(
        libs.plugins.ktMpp.mavenPublish
            .get()
            .pluginId,
    )
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

val generateGrammarSource =
    tasks.named<AntlrTask>("generateGrammarSource") {
        maxHeapSize = "64m"
        arguments = arguments + listOf("-visitor", "-long-messages")
        val buildDir =
            project.layout.buildDirectory
                .get()
                .asFile
        outputDirectory = buildDir.resolve("generated-src/antlr/main/it/unibo/tuprolog/parser")
    }

val generateTestGrammarSource = tasks.named<AntlrTask>("generateTestGrammarSource")

tasks.named("compileKotlin") { dependsOn(generateGrammarSource) }
tasks.named("compileTestKotlin") { dependsOn(generateTestGrammarSource) }

fun dependOnGrammarGeneration(task: Task) {
    if ("Test" in task.name) {
        task.dependsOn(generateTestGrammarSource)
    } else {
        task.dependsOn(generateGrammarSource)
    }
}

tasks.withType<AbstractDokkaTask>().configureEach { dependOnGrammarGeneration(this) }
tasks.withType<Detekt>().configureEach { dependOnGrammarGeneration(this) }
tasks.withType<BaseKtLintCheckTask>().configureEach { dependOnGrammarGeneration(this) }

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    filter {
        exclude("**/generated-src/**")
    }
}

tasks.withType<AbstractDokkaTask>().configureEach {
    dependsOn(generateGrammarSource)
}

tasks.named<Jar>("sourcesJar").configure {
    project.sourceSets.forEach { sourceSet ->
        sourceSet.allSource.sourceDirectories.forEach {
            from(it)
        }
        sourceSet.resources.sourceDirectories.forEach {
            from(it)
        }
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
    dependsOn(generateGrammarSource)
    dependsOn(generateTestGrammarSource)
}
