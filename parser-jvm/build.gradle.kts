plugins {
    antlr
}

dependencies {
    antlr("org.antlr:antlr4:_")
    api("org.antlr:antlr4-runtime:_")

    implementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("test-junit"))
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
