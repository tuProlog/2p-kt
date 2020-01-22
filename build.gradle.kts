import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.dokka.gradle.GradlePassConfigurationImpl
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile

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
val ktFreeCompilerArgs: String by project

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

    // Test Results printing
    tasks.withType<AbstractTestTask> {
        afterSuite(KotlinClosure2({ desc: TestDescriptor, result: TestResult ->
            if (desc.parent == null) { // will match the outermost suite
                println("Results: ${result.resultType} (${result.testCount} tests, ${result.successfulTestCount} successes, ${result.failedTestCount} failures, ${result.skippedTestCount} skipped)")
            }
        }))
    }
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
//                nodejs()
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

    configureDokka()

    configureMavenPublications("packDokka${capitalize(name)}")

    configureUploadToMavenCentral(
        if (version.toString().contains("SNAPSHOT")) {
            "https://oss.sonatype.org/content/repositories/snapshots/"
        } else {
            "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
        }
    )

    configureUploadToBintray("kotlinMultiplatform", "js", "jvm", "metadata")

    configureSigning()
}


fun Project.configureDokka() {
    tasks.withType<DokkaTask> {
        outputDirectory = docDir
        outputFormat = "html"

        multiplatform {
            registerPlatform("jvm")
            registerPlatform("js")
        }
    }

    val jarPlatform = tasks.withType<Jar>().map { it.name.replace("Jar", "") }

    task<DefaultTask>("packAllDokka") {
        group = "documentation"
    }

    jarPlatform.forEach {
        val packDokkaForPlatform = "packDokka${capitalize(it)}"

        task<Jar>(packDokkaForPlatform) {
            group = "documentation"
            dependsOn("dokka")
            from(docDir)
            archiveBaseName.set(project.name)
            archiveVersion.set(project.version.toString())
            archiveAppendix.set(it)
            archiveClassifier.set("javadoc")
        }

        tasks.getByName("${it}Jar").dependsOn(packDokkaForPlatform)
        tasks.getByName("packAllDokka").dependsOn(packDokkaForPlatform)
    }
}

fun Project.configureSigning() {
    signing {
        useInMemoryPgpKeys(signingKey, signingPassword)

        sign(publishing.publications)
    }

    publishing {
        val pubs = publications.withType<MavenPublication>().map { "sign${capitalize(it.name)}Publication" }

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
            setPublications(*publicationNames)
            override = true
            with(pkg) {
                repo = "tuprolog"
                name = project.name
                userOrg = "pika-lab"
                vcsUrl = "https://gitlab.com/pika-lab/tuprolog/2p-in-kotlin"
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

fun Project.configureMavenPublications(docArtifact: String) {
    publishing {
        publications.withType<MavenPublication> {
            groupId = project.group.toString()
            version = project.version.toString()

            if (docArtifact in tasks.names) {
                artifact(tasks.getByName(docArtifact)) {
                    classifier = "javadoc"
                }
            }

            pom {
                name.set("2P in Kotlin -- Module `${this@configureMavenPublications.name}`")
                description.set("Multi-platform Prolog environment, in Kotlin")
                url.set("https://gitlab.com/pika-lab/tuprolog/2p-in-kotlin")
                licenses {
                    license {
                        name.set("Apache 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }

                developers {
                    developer {
                        name.set("Giovanni Ciatto")
                        email.set("giovanni.ciatto@gmail.com")
                        url.set("https://about.me/gciatto")
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
    }
}

fun getPropertyOrWarnForAbsence(key: String): String? {
    val value = property(key)?.toString()
    if (value.isNullOrBlank()) {
        System.err.println("WARNING: $key is not set")
    }
    return value
}

fun capitalize(s: String) = s[0].toUpperCase() + s.substring(1)

fun Set<String>.forEachProject(f: Project.() -> Unit) = subprojects.filter { it.name in this }.forEach(f)

val Project.docDir: String
    get() = "$buildDir/doc"

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
        configuration(this@register)
    }
}

fun NamedDomainObjectContainerScope<GradlePassConfigurationImpl>.registerPlatform(platform: String) =
    registerPlatform(platform) { }

