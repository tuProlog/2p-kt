plugins {
    `kotlin-jvm-only`
    antlr
    `kotlin-doc`
    `publish-on-maven`
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
    outputDirectory = File("${project.buildDir}/generated-src/antlr/main/it/unibo/tuprolog/parser")
    tasks.compileKotlin.orNull?.dependsOn(this)
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
