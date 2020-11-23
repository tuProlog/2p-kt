import de.fayard.dependencies.bootstrapRefreshVersionsAndDependencies

rootProject.name = "2p"

buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        jcenter()
    }
    dependencies {
        classpath("de.fayard:dependencies:0.+")
    }
}

bootstrapRefreshVersionsAndDependencies()

include("documentation")

include("core")
include("unify")
include("theory")
include("data-structures")
include("dsl-core")
include("dsl-unify")
include("dsl-theory")
include("dsl-solve")
include("solve")
include("solve-classic")
include("solve-streams")
include("test-solve")
include("parser-core")
include("parser-jvm")
include("parser-js")
include("parser-theory")
include("prob-solve")
include("problog-solve-classic")
include("serialize-core")
include("serialize-theory")
include("repl")
include("oop-lib")
include("ide")
include("examples")
