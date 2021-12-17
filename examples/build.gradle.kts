dependencies {
    api(project(":solve-classic"))
    api(project(":solve-problog"))
    api(project(":solve-concurrent"))
    api(project(":dsl-theory"))
    api(project(":parser-theory"))
    api(project(":io-lib"))
    api(project(":oop-lib"))
    api(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("test-junit"))
}
