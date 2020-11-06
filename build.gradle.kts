import com.github.breadmoirai.githubreleaseplugin.GithubReleaseTask
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import io.github.gciatto.kt.node.Bugs
import io.github.gciatto.kt.node.NpmPublishPlugin
import io.github.gciatto.kt.node.People
import org.jetbrains.dokka.gradle.DokkaMultiModuleTask
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

plugins {
    kotlin("multiplatform")
    id("maven-publish")
    signing
    id("org.jetbrains.dokka")
    id("com.jfrog.bintray")
    id("org.danilopianini.git-sensitive-semantic-versioning")
    id("com.github.breadmoirai.github-release")
    id("org.jlleitschuh.gradle.ktlint")
    id("io.github.gciatto.kt-npm-publish")
}

repositories {
    maven("https://maven-central-eu.storage-download.googleapis.com/maven2")
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
val projectDescription: String by project
val projectHome: String by project
val projectLicenseUrl: String by project

val mochaTimeout: String by project

val signingKey = getPropertyOrWarnForAbsence("signingKey")
val signingPassword = getPropertyOrWarnForAbsence("signingPassword")
val bintrayUser = getPropertyOrWarnForAbsence("bintrayUser")
val bintrayKey = getPropertyOrWarnForAbsence("bintrayKey")
val ossrhUsername = getPropertyOrWarnForAbsence("ossrhUsername")
val ossrhPassword = getPropertyOrWarnForAbsence("ossrhPassword")
val githubToken = getPropertyOrWarnForAbsence("githubToken")
val npmToken = getPropertyOrWarnForAbsence("npmToken")

val allSubprojects = allprojects.map { it.name }.toSet()
val jvmSubprojects = setOf("parser-jvm", "examples", "ide")
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
//                    api(kotlin("reflect"))
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
                nodejs {
                    testTask {
                        useMocha {
                            timeout = mochaTimeout
                        }
                    }
                }
//                browser()
                tasks.withType<KotlinJsCompile> {
                    kotlinOptions {
                        moduleKind = "umd"
                        // noStdlib = true
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

    configureKtLint()
    configureDokka("jvm", "js")
    configureMavenPublications("packDokka")
    configureUploadToMavenCentral()
    configureUploadToBintray("kotlinMultiplatform", "js", "jvm", "metadata")
    configureSigning()
    configureJsPackage()
    configureUploadToGithub({ "shadow" in it })
}

with(rootProject) {
    kotlin {
        sourceSets {
            val commonMain by getting {
                dependencies {
                    for (subproject in (ktSubprojects - setOf(rootProject.name, "solve-streams", "test-solve"))) {
                        api(project(subproject))
                    }
                }
            }

            val jvmMain by getting {
                dependencies {
                    for (subproject in jvmSubprojects) {
                        api(project(subproject))
                    }
                }
            }

            val jsMain by getting {
                dependencies {
                    for (subproject in jsSubprojects) {
                        api(project(subproject))
                    }
                }
            }
        }
    }

    val dokkaHtmlMultiModule by tasks.getting(DokkaMultiModuleTask::class)
    val packDokkaMultiModule by tasks.registering(Zip::class) {
        group = "documentation"
        dependsOn(dokkaHtmlMultiModule)
        from(dokkaHtmlMultiModule.outputDirectory.get())
        archiveBaseName.set(project.name)
        archiveVersion.set(project.version.toString())
        archiveAppendix.set("documentation")
    }
    configureUploadToGithub(packDokkaMultiModule.get())
}

jvmSubprojects.forEachProject {
    apply(plugin = "maven-publish")
    apply(plugin = "java-library")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "signing")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "com.jfrog.bintray")

    configureKtLint()
    configureDokka()
    createMavenPublications("jvm", "java", docArtifact = "packDokka")
    configureUploadToMavenCentral()
    configureUploadToBintray()
    configureSigning()
    configureUploadToGithub({ "shadow" in it })
}

jsSubprojects.forEachProject {
    apply(plugin = "maven-publish")
    apply(plugin = "org.jetbrains.kotlin.js")
    apply(plugin = "signing")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "com.jfrog.bintray")

    configureKtLint()
    configureDokka()
    createMavenPublications("js", "kotlin", docArtifact = "packDokka")
    configureUploadToMavenCentral()
    configureUploadToBintray()
    configureSigning()
}

githubRelease {
    if (githubToken != null) {
        token(githubToken)
        owner(githubOwner)
        repo(githubRepo)
        tagName { version.toString() }
        releaseName { version.toString() }
        allowUploadToExisting { true }
        prerelease { false }
        draft { false }
//        overwrite { false }
        try {
            body(
                """|## CHANGELOG
                   |${changelog().call()}
                   """.trimMargin()
            )
        } catch (e: Throwable) {
            e.message?.let { warn(it) }
        }
    }
}

fun Project.configureKtLint() {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    ktlint {
        debug.set(false)
        ignoreFailures.set(false)
        enableExperimentalRules.set(true)
        filter {
            exclude("**/generated/**")
            include("**/kotlin/**")
        }
    }
}

fun Project.configureUploadToGithub(
    vararg tasks: Zip
) {
    if (githubToken == null) return

    val archiveFiles = tasks.map { it.archiveFile }

    rootProject.githubRelease {
        releaseAssets(*(releaseAssets.toList() + archiveFiles).toTypedArray())
    }

    rootProject.tasks.withType(GithubReleaseTask::class) {
        dependsOn(*tasks)
    }
}

fun Project.configureUploadToGithub(
    jarTaskPositiveFilter: (String) -> Boolean = { "jar" in it },
    jarTaskNegativeFilter: (String) -> Boolean = { "dokka" in it || "source" in it }
) {
    if (githubToken == null) return

    val zipTasks = tasks.withType(Zip::class).asSequence()
        .filter { jarTaskPositiveFilter(it.name.toLowerCase()) }
        .filter { !jarTaskNegativeFilter(it.name.toLowerCase()) }
        .toList()
        .toTypedArray()

    configureUploadToGithub(*zipTasks)
}

fun Project.configureDokka(vararg platforms: String) {
    tasks.withType<DokkaTask>().configureEach {
        outputDirectory.set(docDir)

        dokkaSourceSets {
            if (platforms.isNotEmpty()) {
                for (p in platforms) {
                    named("${p}Main")
                }
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
                dependsOn("dokkaHtml")
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
            dependsOn("dokkaHtml")
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
                setPublications(
                    *project.publishing.publications.withType<MavenPublication>().map { it.name }
                        .toTypedArray()
                )
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
                if (component in components.names) {
                    from(components[component])
                } else {
                    warn("Missing component $component in ${project.name} for publication $name")
                }
            }

            if (docArtifact != null && docArtifact in tasks.names) {
                artifact(tasks.getByName(docArtifact)) {
                    classifier = "javadoc"
                }
            } else if (docArtifact == null || !docArtifact.endsWith("KotlinMultiplatform")) {
                log(
                    "No javadoc artifact for publication $name in project ${project.name}: " +
                        "no such a task: $docArtifact"
                )
            }

            artifact(sourcesJar)

            configurePom(project.name)
        }
    }
}

fun Set<String>.forEachProject(f: Project.() -> Unit) = allprojects.filter { it.name in this }.forEach(f)

fun Project.configureJsPackage(packageJsonTask: String = "jsPackageJson", compileTask: String = "jsMainClasses") {
    if (this == rootProject) return

    apply<NpmPublishPlugin>()

    npmPublishing {
        defaultValuesFrom(rootProject)

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
}

private val FULL_VERSION_REGEX = "^[0-9]+\\.[0-9]+\\.[0-9]+$".toRegex()

val Project.isFullVersion: Boolean
    get() = version.toString().matches(FULL_VERSION_REGEX)

fun Project.configureTestResultPrinting() {
    tasks.withType<AbstractTestTask> {
        afterSuite(
            KotlinClosure2({ desc: TestDescriptor, result: TestResult ->
                if (desc.parent == null) { // will match the outermost suite
                    println("Results: ${result.resultType} (${result.testCount} tests, ${result.successfulTestCount} successes, ${result.failedTestCount} failures, ${result.skippedTestCount} skipped)")
                }
            })
        )
    }
}

fun MavenPublication.configurePom(projectName: String) {
    pom {
        name.set("2P in Kotlin -- Module `$projectName`")
        description.set(projectDescription)
        url.set(projectHome)
        licenses {
            license {
                name.set(projectLicense)
                url.set(projectLicenseUrl)
            }
        }

        developers {
            developer {
                name.set(gcName)
                email.set(gcEmail)
                url.set(gcUrl)
                organization.set("University of Bologna")
                organizationUrl.set("https://www.unibo.it/it")
            }
            developer {
                name.set("Enrico Siboni")
                email.set("enrico.siboni3@studio.unibo.it")
                url.set("https://www.linkedin.com/in/enrico-siboni/")
            }
        }

        scm {
            connection.set("scm:git:git:///gitlab.com/pika-lab/tuprolog/2p-in-kotlin.git")
            url.set("https://gitlab.com/pika-lab/tuprolog/2p-in-kotlin")
        }
    }
}

fun log(message: String) {
    println("LOG: $message")
}

fun warn(message: String) {
    System.err.println("WARNING: $message")
}

fun Project.getPropertyOrWarnForAbsence(key: String): String? {
    val value = property(key)?.toString()
    if (value.isNullOrBlank()) {
        warn("$key is not set")
    }
    return value
}

val Project.docDir: File
    get() = buildDir.resolve("doc")
