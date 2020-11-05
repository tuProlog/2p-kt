import io.github.gciatto.kt.node.Bugs
import io.github.gciatto.kt.node.People

plugins {
    id("io.github.gciatto.kt-npm-publish")
}

val gcName: String by project
val gcEmail: String by project
val gcUrl: String by project
val projectHomepage: String by project
val projectLicense: String by project
val projectIssues: String by project
val npmToken: String by project

val generatedSrcDir = "$buildDir/generated-src/antlr/main"

kotlin {
    target {
        nodejs()
    }

    with(sourceSets["main"]) {
        dependencies {
            api(kotlin("stdlib-js"))
            api(npm("antlr4", "^${Versions.org_antlr.replace("-1", ".0")}"))
            api(npm("@tuprolog/parser-utils", "0.2.2"))
        }
    }

    with(sourceSets["test"]) {
        dependencies {
            implementation(kotlin("test-js"))
        }
    }
}

npmPublishing {
    defaultValuesFrom(rootProject)
    // packageJson.set(tasks.getByName<KotlinPackageJsonTask>("packageJson").packageJson)

    liftPackageJson {
        people = mutableListOf(People(gcName, gcEmail, gcUrl))
        homepage = projectHomepage
        bugs = Bugs(projectIssues, gcEmail)
        license = projectLicense
        name = "@tuprolog/$name"
        dependencies = dependencies?.filterKeys { key -> "kotlin-test" !in key }
            ?.mapKeys { (key, _) ->
                if ("2p" in key) "@tuprolog/$key" else key
            }?.mapValues { (key, value) ->
                val temp = if (value.startsWith("file:")) {
                    value.split('/', '\\').last()
                } else {
                    value
                }
                if ("2p" in key) temp.substringBefore('+') else temp
            }?.toMutableMap()
        version = version?.substringBefore('+')
    }

    liftJsSources { _, _, line ->
        line.replace("'2p", "'@tuprolog/2p")
            .replace("\"2p", "\"@tuprolog/2p")
    }
}

tasks.create("jsTest") {
    dependsOn("test")
}

publishing {
    publications.withType<MavenPublication>().getByName("js") {
        from(components["kotlin"])
    }
}

tasks.getByName<Jar>("sourcesJar") {
    kotlin.sourceSets.forEach { sourceSet ->
        sourceSet.kotlin.srcDirs.forEach {
            from(it)
        }
    }
}
