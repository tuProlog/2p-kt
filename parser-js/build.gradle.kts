import node.Bugs
import node.NpmPublishExtension
import node.NpmPublishPlugin
import node.People
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsSetupTask
import org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinPackageJsonTask

apply<NpmPublishPlugin>()

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

configure<NpmPublishExtension> {
    nodeRoot = rootProject.tasks.withType<NodeJsSetupTask>().asSequence().map { it.destination }.first()
    token = npmToken
    packageJson = tasks.getByName<KotlinPackageJsonTask>("packageJson").packageJson
    nodeSetupTask = rootProject.tasks.getByName("kotlinNodeJsSetup").path
    jsCompileTask = "mainClasses"

    liftPackageJson {
        people = mutableListOf(People(gcName, gcEmail, gcUrl))
        homepage = projectHomepage
        bugs = Bugs(projectIssues, "gcEmail")
        license = projectLicense
        name = "@tuprolog/$name"
        dependencies = dependencies?.mapKeys {
            if ("2p" in it.key) "@tuprolog/${it.key}" else it.key
        }?.toMutableMap()
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
