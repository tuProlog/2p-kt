import Developer.Companion.getAllDevs

plugins {
    `maven-publish`
    signing
}

val projectLongName: String by project
val projectDescription: String by project
val projectHomepage: String by project
val projectLicense: String? by project
val projectLicenseUrl: String? by project
val scmUrl: String? by project
val scmConnection: String? by project
val issuesUrl: String? by project
val issuesEmail: String? by project
// env ORG_GRADLE_PROJECT_signingKey
val signingKey: String? by project
// env ORG_GRADLE_PROJECT_signingPassword
val signingPassword: String? by project
// env ORG_GRADLE_PROJECT_mavenRepo
val mavenRepo: String? by project
// env ORG_GRADLE_PROJECT_mavenUsername
val mavenUsername: String? by project
// env ORG_GRADLE_PROJECT_mavenPassword
val mavenPassword: String? by project

val publishableClassifiers = setOf("javadoc")

publishing {
    repositories {
        maven {
            mavenRepo?.let { url = uri(it) }
            if (mavenUsername != null && mavenPassword != null) {
                credentials {
                    username = mavenUsername
                    password = mavenPassword
                }
            }
        }
    }

    plugins.withId("org.jetbrains.kotlin.jvm") {
        publications.maybeCreate<MavenPublication>("jvm").run {
            from(components["java"])
        }
    }

    plugins.withId("org.jetbrains.kotlin.js") {
        publications.maybeCreate<MavenPublication>("js").run {
            from(components["kotlin"])
        }
    }

    project.afterEvaluate {
        publications.withType<MavenPublication> {
            groupId = project.group.toString()
            version = project.version.toString()

            tasks.withType<Jar> {
                if ("Html" in name && archiveClassifier.getOrElse("") in publishableClassifiers) {
                    artifact(this)
                }
            }

            pom {
                name.set(projectLongName)
                description.set(projectDescription)
                url.set(projectHomepage)
                licenses {
                    license {
                        name.set(projectLicense)
                        url.set(projectLicenseUrl)
                    }
                }

                developers {
                    for (dev in project.getAllDevs()) {
                        developer {
                            name.set(dev.name)
                            dev.email?.let { email.set(it) }
                            dev.url?.let { url.set(it) }
                            dev.org?.let {
                                organization.set(it.name)
                                organization.set(it.url)
                            }

                        }
                    }
                }

                scm {
                    connection.set(scmConnection)
                    url.set(scmUrl)
                }
            }
        }
    }

    signing {
        if (arrayOf(signingKey, signingPassword).none { it.isNullOrBlank() }) {
            useInMemoryPgpKeys(signingKey, signingPassword)
            sign(publications)
        }

        val signAll = project.tasks.create("signAllPublications")
        project.tasks.withType<Sign> {
            signAll.dependsOn(this)
        }
    }
}
