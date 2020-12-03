plugins {
    kotlin("js") apply false
}

kotlin {

    println(sourceSets.forEach(::println))

    with(sourceSets["main"]) {
        dependencies {
            api(kotlin("stdlib-js"))
            api(npm("antlr4", "4.8.0"))
            api(npm("@tuprolog/parser-utils", "0.2.3"))
        }
    }

    with(sourceSets["test"]) {
        dependencies {
            implementation(kotlin("test-js"))
        }
    }
}
