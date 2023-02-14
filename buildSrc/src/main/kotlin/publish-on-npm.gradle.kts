import Developer.Companion.getAllDevs
import dev.petuska.npm.publish.NpmPublishPlugin
import dev.petuska.npm.publish.extension.NpmPublishExtension

apply<NpmPublishPlugin>()

val projectLongName: String? by project
val projectDescription: String? by project
val projectHomepage: String? by project
val projectLicense: String? by project
val projectLicenseUrl: String? by project
val scmUrl: String? by project
val scmConnection: String? by project
val issuesUrl: String? by project
val issuesEmail: String? by project
val npmToken: String? by project
val npmRepo: String? by project
val npmDryRun: String? by project
val npmOrganization: String? by project

configure<NpmPublishExtension> {
    npmOrganization?.let { organization.set(it) }
    readme.set(rootProject.file("README.md"))
    // bundleKotlinDependencies.set(true)
    dry.set(npmDryRun?.let { it.toBoolean() } ?: false)
    registries {
        npmjs {
            npmToken?.let { authToken.set(it) }
        }
    }
    packages {
        all {
            packageName.set("${rootProject.name}-${project.name}")
            packageJson {
                homepage.set(projectHomepage)
                description.set(projectDescription)
                val developers = project.getAllDevs()
                if (developers.isNotEmpty()) {
                    author.set(person(developers.first()))
                }
                contributors.set(
                    developers.asSequence()
                        .drop(1)
                        .map { person(it) }
                        .toCollection(mutableListOf())
                )
                license.set(projectLicense)
                private.set(false)
                bugs {
                    url.set(issuesUrl)
                    email.set(issuesEmail)
                }
                repository {
                    type.set("git")
                    url.set(scmUrl)
                }
            }
        }
    }
}
