plugins {
    id(
        libs.plugins.ktMpp.mavenPublish
            .get()
            .pluginId,
    )
}

dependencies {
    api(project(":solve-classic"))
    api(project(":solve-concurrent"))
    api(project(":solve-problog"))
    api(project(":dsl-theory"))
    api(project(":parser-theory"))
    api(project(":io-lib"))
    api(project(":oop-lib"))
    api(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("test-junit"))
}
