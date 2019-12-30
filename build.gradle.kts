import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.dokka.gradle.GradlePassConfigurationImpl
import org.jetbrains.kotlin.gradle.dsl.KotlinJsCompile

plugins {
    kotlin("multiplatform") version "1.3.61" // keep this value aligned with the one in gradle.properties
    id("maven-publish")
    signing
    id("org.jetbrains.dokka") version "0.10.0"
    id("com.jfrog.bintray") version "1.8.4"
    id("org.danilopianini.git-sensitive-semantic-versioning") version "0.2.2"
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

val publishAllToBintrayTask = tasks.create<DefaultTask>("publishAllToBintray") {
    group = "publishing"
}

fun getPropertyOrWarnForAbsence(key: String): String? {
    val value = property(key)?.toString()
    if (value.isNullOrBlank()) {
        System.err.println("WARNING: $key is not set")
    }
    return value
}

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

val Project.docDir: String
    get() = "$buildDir/doc"

fun capitalize(s: String): String {
    return s[0].toUpperCase() + s.substring(1)
}

fun NamedDomainObjectContainerScope<GradlePassConfigurationImpl>.registerPlatform(
    platform: String, configuration: Action<in GradlePassConfigurationImpl>) {

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

// apply next commands to all subprojects
subprojects {

    group = rootProject.group
    version = rootProject.version

    // ** NOTE ** legacy plugin application, because the new "plugins" block is not available inside "subprojects" scope yet
    // when it will be available it should be moved here
    apply(plugin = "org.jetbrains.kotlin.multiplatform")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "org.jetbrains.dokka")
    apply(plugin = "com.jfrog.bintray")

    // projects dependencies repositories
    repositories {
        mavenCentral()
        maven("https://dl.bintray.com/kotlin/dokka")
    }

    // Common kotlin multiplatform configuration for sub-projects
    kotlin {

        sourceSets {
            val commonMain by getting {
                dependencies {
                    implementation(kotlin("stdlib-common"))
//                    implementation(kotlin("reflect"))
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
                        implementation(kotlin("stdlib-jdk8"))
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

    // Test Results printing
    tasks.withType<AbstractTestTask> {
        afterSuite(KotlinClosure2({ desc: TestDescriptor, result: TestResult ->
            if (desc.parent == null) { // will match the outermost suite
                println("Results: ${result.resultType} (${result.testCount} tests, ${result.successfulTestCount} successes, ${result.failedTestCount} failures, ${result.skippedTestCount} skipped)")
            }
        }))
    }

    tasks.withType<DokkaTask> {
        outputDirectory = docDir
        outputFormat = "html"

        multiplatform {
            registerPlatform("jvm") {
                jdkVersion = 6
            }

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

    // https://central.sonatype.org/pages/requirements.html
    // https://docs.gradle.org/current/userguide/signing_plugin.html
    publishing {
        publications.withType<MavenPublication> {
            groupId = project.group.toString()
            version = project.version.toString()

            val docArtifact = "packDokka${capitalize(name)}"

            if (docArtifact in tasks.names) {
                artifact(tasks.getByName(docArtifact)) {
                    classifier = "javadoc"
                }
            }

            pom {
                name.set("2P in Kotlin -- Module `${this@subprojects.name}`")
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
                        email.set("siboxd@gmail.com")
                        url.set("https://github.com/siboXD")
                    }
                }

                scm {
                    connection.set("scm:git:git:///gitlab.com/pika-lab/tuprolog/2p-in-kotlin.git")
                    url.set("https://gitlab.com/pika-lab/tuprolog/2p-in-kotlin")
                }
            }

        }

//        repositories {
//            val mavenRepoUrl = if (version.toString().contains("SNAPSHOT")) {
//                "https://oss.sonatype.org/content/repositories/snapshots/"
//            } else {
//                "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
//            }
//
//            maven(mavenRepoUrl) {
//                credentials {
//                    username = project.property("ossrhUsername").toString()
//                    password = project.property("ossrhPassword").toString()
//                }
//            }
//        }

        bintray {
            user = bintrayUser
            key = bintrayKey
            setPublications("kotlinMultiplatform", "js", "jvm", "metadata")
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

        tasks.withType<BintrayUploadTask> {
            publishAllToBintrayTask.dependsOn(this)
        }
    }

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

tasks.withType<DokkaTask> {
    outputDirectory = docDir
    outputFormat = "html"

    multiplatform {
        listOf("jvm", "js").forEach { platform ->
            registerPlatform(platform) {
                rootProject.subprojects.forEach { subproject ->
                    listOf("${platform}Main", "commonMain").forEach { sourceSet ->
                        subproject.kotlin.sourceSets.getByName(sourceSet).kotlin.srcDirs
                            .filter { it.exists() }
                            .map { it.toString() }
                            .forEach {
                                this@registerPlatform.sourceRoot {
                                    path = it
                                }
                            }
                    }
                }
            }
        }
    }
}