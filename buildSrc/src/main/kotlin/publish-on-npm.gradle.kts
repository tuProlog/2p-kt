import Developer.Companion.getAllDevs
import dev.petuska.npm.publish.NpmPublishPlugin
import dev.petuska.npm.publish.dsl.NpmPublishExtension

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
// env ORG_GRADLE_PROJECT_npmToken
val npmToken: String? by project
val npmRepo: String? by project
val npmDryRun: String? by project

configure<NpmPublishExtension> {
    readme = file("README.md")
    bundleKotlinDependencies = true
    dry = npmDryRun?.let { it.toBoolean() } ?: false
    repositories {
        repository("npm") {
            registry = uri(npmRepo ?: "https://registry.npmjs.org")
            npmToken?.let { authToken = it }
        }
    }
    publications {
        all {
            packageJson {
                homepage = projectHomepage
                description = projectDescription
                val developers = project.getAllDevs()
                if (developers.isNotEmpty()) {
                    author = developers.first().toPerson()
                }
                contributors = developers.asSequence()
                    .drop(1)
                    .map { Person(it.toPerson()) }
                    .toCollection(mutableListOf())
                license = projectLicense
                private = false
                bugs {
                    url = issuesUrl
                    email = issuesEmail
                }
                repository {
                    type = "git"
                    url = scmUrl
                }
            }
        }
    }
}
