plugins {
    id("com.gradle.enterprise") version "3.11.3"
}

rootProject.name = "2p"

enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
}

include(":documentation")
include(":utils")
include(":core")
include(":unify")
include(":theory")
include(":bdd")
include(":dsl-core")
include(":dsl-unify")
include(":dsl-theory")
include(":dsl-solve")
include(":solve")
include(":solve-classic")
include(":solve-streams")
include(":solve-concurrent")
include(":test-solve")
include(":parser-core")
include(":parser-jvm")
include(":parser-js")
include(":parser-theory")
include(":solve-plp")
include(":solve-problog")
include(":serialize-core")
include(":serialize-theory")
include(":repl")
include(":oop-lib")
include(":io-lib")
include(":ide-plp")
include(":ide")
include(":examples")
include(":full")

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishOnFailure()
    }
}
