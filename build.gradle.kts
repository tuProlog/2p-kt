import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.dokka.gradle.GradlePassConfigurationImpl
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile
import org.jetbrains.kotlin.gradle.targets.js.npm.tasks.KotlinPackageJsonTask
import node.*

plugins {
    kotlin("multiplatform") version Versions.org_jetbrains_kotlin_multiplatform_gradle_plugin
    id("maven-publish")
    signing
    id("org.jetbrains.dokka") version Versions.org_jetbrains_dokka_gradle_plugin
    id("com.jfrog.bintray") version Versions.com_jfrog_bintray_gradle_plugin
    id("org.danilopianini.git-sensitive-semantic-versioning") version Versions.org_danilopianini_git_sensitive_semantic_versioning_gradle_plugin
    id("de.fayard.buildSrcVersions") version Versions.de_fayard_buildsrcversions_gradle_plugin
}

repositories {
    mavenCentral()
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

// env ORG_GRADLE_PROJECT_signingKey
val signingKey = getPropertyOrWarnForAbsence("signingKey")
// env ORG_GRADLE_PROJECT_signingPassword
val signingPassword = getPropertyOrWarnForAbsence("signingPassword")
// env ORG_GRADLE_PROJECT_bintrayUser
val bintrayUser = getPropertyOrWarnForAbsence("bintrayUser")
// env ORG_GRADLE_PROJECT_bintrayKey
val bintrayKey = getPropertyOrWarnForAbsence("bintrayKey")
// env ORG_GRADLE_PROJECT_ossrhUsername
val ossrhUsername = getPropertyOrWarnForAbsence("ossrhUsername")
// env ORG_GRADLE_PROJECT_ossrhPassword
val ossrhPassword = getPropertyOrWarnForAbsence("ossrhPassword")

val allSubprojects = subprojects.map { it.name  }.toSet()
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

    repositories {
        mavenCentral()
        maven("https://dl.bintray.com/kotlin/dokka")
    }

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

            // Default source set for JVM-specific sources and dependencies:
            jvm {

                compilations["main"].defaultSourceSet {
                    dependencies {
                        api(kotlin("stdlib-jdk8"))
                    }
                }

                // JVM-specific tests and their dependencies:
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
                sequenceOf("", "Test").forEach {
                    tasks.getByName<KotlinJsCompile>("compile${it}KotlinJs") {
                        kotlinOptions {
                            moduleKind = "umd"
                            //noStdlib = true
                            metaInfo = true
                            sourceMap = true
                            sourceMapEmbedSources = "always"
                        }
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

    configureUploadToMavenCentral(
        if (version.toString().contains("SNAPSHOT")) {
            "https://oss.sonatype.org/content/repositories/snapshots/"
        } else {
            "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
        }
    )

    configureUploadToBintray("kotlinMultiplatform", "js", "jvm", "metadata")

    configureSigning()

    configureJsPackage()
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

    configureUploadToMavenCentral(
        if (version.toString().contains("SNAPSHOT")) {
            "https://oss.sonatype.org/content/repositories/snapshots/"
        } else {
            "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
        }
    )

    configureUploadToBintray()

    configureSigning()
}

jsSubprojects.forEachProject {
    apply(plugin = "maven-publish")
    apply(plugin = "org.jetbrains.kotlin.js")
    apply(plugin = "signing")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "com.jfrog.bintray")

    configureDokka()

    createMavenPublications("jvm", "kotlin", docArtifact = "packDokka")

    configureUploadToMavenCentral(
        if (version.toString().contains("SNAPSHOT")) {
            "https://oss.sonatype.org/content/repositories/snapshots/"
        } else {
            "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
        }
    )

    configureUploadToBintray()

    configureSigning()

    configureJsPackage()
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

    task<DefaultTask>("packAllDokka") {
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

//            tasks.getByName("${it}Jar").dependsOn(packDokkaForPlatform)
            tasks.getByName("packAllDokka").dependsOn(packDokkaForPlatform)
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

        tasks.getByName("packAllDokka").dependsOn(packDokka)
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
                setPublications(*this@configureUploadToBintray.publishing.publications.withType<MavenPublication>().map { it.name }.toTypedArray())
            } else {
                setPublications(*publicationNames)
            }
            override = true
            with(pkg) {
                repo = "tuprolog"
                name = project.name
                userOrg = "pika-lab"
                vcsUrl = "https://github.com/tuProlog/2p-kt"
                setLicenses("Apache-2.0")
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

fun Project.configureUploadToMavenCentral(mavenRepoUrl: String) {
    if (ossrhUsername != null && ossrhPassword != null) {
        publishing {
            repositories {
                maven(mavenRepoUrl) {
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
            groupId = this@configureMavenPublications.group.toString()
            version = this@configureMavenPublications.version.toString()

            val docArtifact = "${docArtifactBaseName}${name.capitalize()}"

            if (docArtifact in tasks.names) {
                artifact(tasks.getByName(docArtifact)) {
                    classifier = "javadoc"
                }
            } else if (!docArtifact.endsWith("KotlinMultiplatform")) {
                log("no javadoc artifact for publication $name in project ${this@configureMavenPublications.name}: " +
                        "no such a task: $docArtifact")
            }

            configurePom(this@configureMavenPublications.name)

        }
    }
}

fun Project.createMavenPublications(name: String, vararg componentsStrings: String, docArtifact: String? = null) {

    val sourcesJar by tasks.creating(Jar::class) {
        archiveBaseName.set(this@createMavenPublications.name)
        archiveVersion.set(this@createMavenPublications.version.toString())
        archiveClassifier.set("sources")
    }

    publishing {
        publications.create<MavenPublication>(name) {
            groupId = this@createMavenPublications.group.toString()
            version = this@createMavenPublications.version.toString()

            for (component in componentsStrings) {
                from(components[component])
            }

            if (docArtifact != null && docArtifact in tasks.names) {
                artifact(tasks.getByName(docArtifact)) {
                    classifier = "javadoc"
                }
            } else if (docArtifact == null || !docArtifact.endsWith("KotlinMultiplatform")) {
                log("no javadoc artifact for publication $name in project ${this@createMavenPublications.name}: " +
                        "no such a task: $docArtifact")
            }

            artifact(sourcesJar)

            configurePom(this@createMavenPublications.name)
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

fun Project.configureJsPackage() {
    val packageJsonTasks = tasks.withType<KotlinPackageJsonTask>().toList()

    packageJsonTasks.forEach {
        val copyReadme = tasks.create<Copy>("copyFilesNextTo${it.name.capitalize()}") {
            group = "nodejs"
            from(rootProject.projectDir)
            include("README*")
            include("CONTRIB*")
            include("LICENSE*")
            into(it.packageJson.parent)
        }
        tasks.create<LiftPackageJsonTask>("lift${it.name.capitalize()}") {
            dependsOn(it)
            dependsOn(copyReadme)
            packageJsonFile = it.packageJson
            lift {
                name = "@tuprolog/$name"
                license = "Apache-2.0"
                people = mutableListOf(
                    People(
                        "Giovanni Ciatto",
                        "giovanni.ciatto@unibo.it",
                        "https://about.me/gciatto"
                    )
                )
                homepage = "https://github.com/tuProlog/2p-kt"
                bugs = Bugs(
                    "https://gitlab.com/pika-lab/tuprolog/2p-in-kotlin/-/issues",
                    "giovanni.ciatto@unibo.it"
                )
            }
        }

    }
}