import com.github.breadmoirai.githubreleaseplugin.GithubReleaseExtension
import com.github.breadmoirai.githubreleaseplugin.GithubReleaseTask
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import node.Bugs
import node.NpmPublishExtension
import node.NpmPublishPlugin
import node.People
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.dokka.gradle.GradlePassConfigurationImpl
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile
import org.jetbrains.kotlin.gradle.targets.js.nodejs.NodeJsSetupTask
import org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinPackageJsonTask
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

plugins {
    kotlin("multiplatform") version Versions.org_jetbrains_kotlin_multiplatform_gradle_plugin
    id("maven-publish")
    signing
    id("org.jetbrains.dokka") version Versions.org_jetbrains_dokka_gradle_plugin
    id("com.jfrog.bintray") version Versions.com_jfrog_bintray_gradle_plugin
    id("org.danilopianini.git-sensitive-semantic-versioning") version Versions.org_danilopianini_git_sensitive_semantic_versioning_gradle_plugin
    id("de.fayard.buildSrcVersions") version Versions.de_fayard_buildsrcversions_gradle_plugin
    id("com.github.breadmoirai.github-release") version Versions.com_github_breadmoirai_github_release_gradle_plugin
}

repositories {
    mavenCentral()
    jcenter()
    maven("https://dl.bintray.com/kotlin/dokka")
}

group = "it.unibo.tuprolog"

gitSemVer {
    minimumVersion.set("0.1.0")
    developmentIdentifier.set("dev")
    noTagIdentifier.set("archeo")
    developmentCounterLength.set(2) // How many digits after `dev`
    version = computeGitSemVer() // THIS IS MANDATORY, AND MUST BE LAST IN THIS BLOCK!
}

println("2p-Kt version: $version")

val javaVersion: String by project
val ktFreeCompilerArgsJvm: String by project
val gcName: String by project
val gcEmail: String by project
val gcUrl: String by project
val projectHomepage: String by project
val bintrayRepo: String by project
val bintrayUserOrg: String by project
val projectLicense: String by project
val projectIssues: String by project
val githubOwner: String by project
val githubRepo: String by project

val signingKey = getPropertyOrWarnForAbsence("signingKey")
val signingPassword = getPropertyOrWarnForAbsence("signingPassword")
val bintrayUser = getPropertyOrWarnForAbsence("bintrayUser")
val bintrayKey = getPropertyOrWarnForAbsence("bintrayKey")
val ossrhUsername = getPropertyOrWarnForAbsence("ossrhUsername")
val ossrhPassword = getPropertyOrWarnForAbsence("ossrhPassword")
val githubToken = getPropertyOrWarnForAbsence("githubToken")
val npmToken = getPropertyOrWarnForAbsence("npmToken")

val allSubprojects = subprojects.map { it.name }.toSet()
val jvmSubprojects = setOf("parser-jvm")
val jsSubprojects = setOf("parser-js")
val docSubprojects = setOf("documentation")

val ktSubprojects = allSubprojects - jvmSubprojects - jsSubprojects - docSubprojects
val codeSubprojects = allSubprojects - docSubprojects

val publishAllToBintrayTask = tasks.create<DefaultTask>("publishAllToBintray") {
    group = "publishing"
}

allSubprojects.forEachProject {
    group = rootProject.group
    version = rootProject.version

    repositories.addAll(rootProject.repositories)

    configureTestResultPrinting()
}

ktSubprojects.forEachProject {

    apply(plugin = "org.jetbrains.kotlin.multiplatform")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "com.jfrog.bintray")

    kotlin {

        sourceSets {
            val commonMain by getting {
                dependencies {
                    api(kotlin("stdlib-common"))
                    api(kotlin("reflect"))
                }
            }
            val commonTest by getting {
                dependencies {
                    implementation(kotlin("test-common"))
                    implementation(kotlin("test-annotations-common"))
                }
            }

            jvm {
                compilations["main"].defaultSourceSet {
                    dependencies {
                        api(kotlin("stdlib-jdk8"))
                    }
                }
                compilations["test"].defaultSourceSet {
                    dependencies {
                        implementation(kotlin("test-junit"))
                    }
                }

                mavenPublication {
                    artifactId = project.name + "-jvm"
                }
            }

            js {
                nodejs()
//                browser()
                tasks.withType<KotlinJsCompile> {
                    kotlinOptions {
                        moduleKind = "umd"
                        //noStdlib = true
                        metaInfo = true
                        sourceMap = true
                        sourceMapEmbedSources = "always"
                    }
                }
                compilations["main"].defaultSourceSet {
                    dependencies {
                        implementation(kotlin("stdlib-js"))
                    }
                }
                compilations["test"].defaultSourceSet {
                    dependencies {
                        implementation(kotlin("test-js"))
                    }
                }

                mavenPublication {
                    artifactId = project.name + "-js"
                }
            }
        }

    }

    tasks.withType<KotlinJvmCompile> {
        kotlinOptions {
            kotlinOptions {
                jvmTarget = "1.$javaVersion"
                freeCompilerArgs = ktFreeCompilerArgsJvm.split(';').toList()
            }
        }
    }

    configureDokka("jvm", "js")
    configureMavenPublications("packDokka")
    configureUploadToMavenCentral()
    configureUploadToBintray("kotlinMultiplatform", "js", "jvm", "metadata")
    configureSigning()
    configureJsPackage()
    configureUploadToGithub({ "jvm" in it })
}

jvmSubprojects.forEachProject {
    apply(plugin = "maven-publish")
    apply(plugin = "java-library")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "signing")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "com.jfrog.bintray")

    configureDokka()
    createMavenPublications("jvm", "java", docArtifact = "packDokka")
    configureUploadToMavenCentral()
    configureUploadToBintray()
    configureSigning()
    configureUploadToGithub()
}

jsSubprojects.forEachProject {
    apply(plugin = "maven-publish")
    apply(plugin = "org.jetbrains.kotlin.js")
    apply(plugin = "signing")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "com.jfrog.bintray")

    configureDokka()
    createMavenPublications("js", "kotlin", docArtifact = "packDokka")
    configureUploadToMavenCentral()
    configureUploadToBintray()
    configureSigning()
}

configure<GithubReleaseExtension> {
    token(githubToken)
    owner(githubOwner)
    repo(githubRepo)
    tagName { version.toString() }
    releaseName { version.toString() }
    allowUploadToExisting { true }
    prerelease { !isFullVersion }
    draft { false }
    body(
        """|## CHANGELOG
            |${changelog().call()}
            """.trimMargin()
    )
}

fun Project.configureUploadToGithub(
    jarTaskPositiveFilter: (String) -> Boolean = { "jar" in it },
    jarTaskNegativeFilter: (String) -> Boolean = { "dokka" in it || "source" in it }
) {
    val jarTasks = tasks.withType(Jar::class).asSequence()
        .filter { jarTaskPositiveFilter(it.name.toLowerCase()) }
        .filter { !jarTaskNegativeFilter(it.name.toLowerCase()) }
        .map { it.archiveFile }
        .toList()

    rootProject.configure<GithubReleaseExtension> {
        releaseAssets(*(releaseAssets.toList() + jarTasks).toTypedArray())
    }

    rootProject.tasks.withType(GithubReleaseTask::class) {
        dependsOn(*jarTasks.toTypedArray())
    }
}

fun Project.configureDokka(vararg platforms: String) {
    tasks.withType<DokkaTask> {
        outputDirectory = docDir
        outputFormat = "html"

        if (platforms.isNotEmpty()) {
            multiplatform {
                platforms.forEach { registerPlatform(it) }
            }
        }
    }

    val packAllDokka: DefaultTask by tasks.creating(DefaultTask::class.java) {
        group = "documentation"
    }

    if (platforms.isNotEmpty()) {
        val jarPlatform = tasks.withType<Jar>().map { it.name.replace("Jar", "") }

        jarPlatform.forEach {
            val packDokkaForPlatform = "packDokka${it.capitalize()}"

            task<Jar>(packDokkaForPlatform) {
                group = "documentation"
                dependsOn("dokka")
                from(docDir)
                archiveBaseName.set(project.name)
                archiveVersion.set(project.version.toString())
                archiveAppendix.set(it)
                archiveClassifier.set("javadoc")
            }

            packAllDokka.dependsOn(packDokkaForPlatform)
        }
    } else {
        val packDokka by tasks.creating(Jar::class) {
            group = "documentation"
            dependsOn("dokka")
            from(docDir)
            archiveBaseName.set(project.name)
            archiveVersion.set(project.version.toString())
            archiveClassifier.set("javadoc")
        }

        packAllDokka.dependsOn(packDokka)
    }
}

fun Project.configureSigning() {
    signing {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications)
    }

    publishing {
        val pubs = publications.withType<MavenPublication>().map { "sign${it.name.capitalize()}Publication" }

        task<Sign>("signAllPublications") {
            dependsOn(*pubs.toTypedArray())
        }
    }
}

fun Project.configureUploadToBintray(vararg publicationNames: String) {
    publishing {
        bintray {
            user = bintrayUser
            key = bintrayKey
            if (publicationNames.isEmpty()) {
                setPublications(*project.publishing.publications.withType<MavenPublication>().map { it.name }
                    .toTypedArray())
            } else {
                setPublications(*publicationNames)
            }
            override = true
            with(pkg) {
                repo = bintrayRepo
                name = project.name
                userOrg = bintrayUserOrg
                vcsUrl = projectHomepage
                setLicenses(projectLicense)
                with(version) {
                    name = project.version.toString()
                }
            }
        }
    }
    this.tasks.withType<BintrayUploadTask> {
        publishAllToBintrayTask.dependsOn(this)
    }
}

fun Project.configureUploadToMavenCentral() {
    val sonatypeUrl: String by this

    if (ossrhUsername != null && ossrhPassword != null) {
        publishing {
            repositories {
                maven(sonatypeUrl) {
                    credentials {
                        username = ossrhUsername
                        password = ossrhPassword
                    }
                }
            }
        }
    }
}

fun Project.configureMavenPublications(docArtifactBaseName: String) {
    publishing {
        publications.withType<MavenPublication> {
            groupId = project.group.toString()
            version = project.version.toString()

            val docArtifact = "${docArtifactBaseName}${name.capitalize()}"

            if (docArtifact in tasks.names) {
                artifact(tasks.getByName(docArtifact)) {
                    classifier = "javadoc"
                }
            } else if (!docArtifact.endsWith("KotlinMultiplatform")) {
                log(
                    "no javadoc artifact for publication $name in project ${project.name}: " +
                            "no such a task: $docArtifact"
                )
            }

            configurePom(project.name)
        }
    }
}

fun Project.createMavenPublications(name: String, vararg componentsStrings: String, docArtifact: String? = null) {

    val sourcesJar by tasks.creating(Jar::class) {
        archiveBaseName.set(project.name)
        archiveVersion.set(project.version.toString())
        archiveClassifier.set("sources")
    }

    publishing {
        publications.create<MavenPublication>(name) {
            groupId = project.group.toString()
            version = project.version.toString()

            for (component in componentsStrings) {
                from(components[component])
            }

            if (docArtifact != null && docArtifact in tasks.names) {
                artifact(tasks.getByName(docArtifact)) {
                    classifier = "javadoc"
                }
            } else if (docArtifact == null || !docArtifact.endsWith("KotlinMultiplatform")) {
                log(
                    "no javadoc artifact for publication $name in project ${project.name}: " +
                            "no such a task: $docArtifact"
                )
            }

            artifact(sourcesJar)

            configurePom(project.name)
        }
    }
}

fun Set<String>.forEachProject(f: Project.() -> Unit) = subprojects.filter { it.name in this }.forEach(f)

fun NamedDomainObjectContainerScope<GradlePassConfigurationImpl>.registerPlatform(
    platform: String, configuration: Action<in GradlePassConfigurationImpl>
) {

    val low = platform.toLowerCase()
    val up = platform.toUpperCase()

    register(low) {
        targets = listOf(up)
        this@register.platform = low
        includeNonPublic = false
        reportUndocumented = false
        collectInheritedExtensionsFromLibraries = true
        skipEmptyPackages = true
        noStdlibLink = true
        noJdkLink = true
        configuration(this@register)
    }
}

fun NamedDomainObjectContainerScope<GradlePassConfigurationImpl>.registerPlatform(platform: String) =
    registerPlatform(platform) { }

fun Project.configureJsPackage(packageJsonTask: String = "jsPackageJson", compileTask: String = "jsMainClasses") {
    apply<NpmPublishPlugin>()

    configure<NpmPublishExtension> {
        nodeRoot = rootProject.tasks.withType<NodeJsSetupTask>().asSequence().map { it.destination }.first()
        token = npmToken ?: ""
        packageJson = tasks.getByName<KotlinPackageJsonTask>(packageJsonTask).packageJson
        nodeSetupTask = rootProject.tasks.getByName("kotlinNodeJsSetup").path
        jsCompileTask = compileTask
        jsSourcesDir = tasks.withType<Kotlin2JsCompile>().asSequence()
            .filter { "Test" !in it.name }
            .map { it.outputFile.parentFile }
            .first()

        liftPackageJson {
            people = mutableListOf(People(gcName, gcEmail, gcUrl))
            homepage = projectHomepage
            bugs = Bugs(projectIssues, gcEmail)
            license = projectLicense
            name = "@tuprolog/$name"
            dependencies = dependencies?.mapKeys {
                if ("2p" in it.key) "@tuprolog/${it.key}" else it.key
            }?.mapValues {
                if ("2p" in it.key) it.value.substringBefore('+') else it.value
            }?.toMutableMap()
            version = version?.substringBefore('+')
        }

        liftJsSources { _, _, line ->
            line.replace("'2p", "'@tuprolog/2p")
                .replace("\"2p", "\"@tuprolog/2p")
        }
    }
}