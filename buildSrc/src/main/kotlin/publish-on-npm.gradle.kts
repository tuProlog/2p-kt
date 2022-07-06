import Developer.Companion.getAllDevs
import io.github.gciatto.kt.node.Bugs
import io.github.gciatto.kt.node.LiftJsSourcesTask
import io.github.gciatto.kt.node.LiftPackageJsonTask
import io.github.gciatto.kt.node.NpmPublishExtension
import io.github.gciatto.kt.node.NpmPublishPlugin
import io.github.gciatto.kt.node.NpmPublishTask
import io.github.gciatto.kt.node.PackageJson

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
val npmOrganization: String? by project

configure<NpmPublishExtension> {
    npmToken?.let { token.set(it) }
    packageJson {
        homepage = projectHomepage
        description = projectDescription
        bugs = Bugs(issuesUrl, issuesEmail)
        license = projectLicense
        liftPackageJsonToFixDependencies(this)
        if (npmOrganization != null) {
            liftPackageJsonToSetOrganization(npmOrganization!!, this)
        }
    }
    if (npmOrganization != null) {
        liftJsSources { _, _, line ->
            liftJsSourcesToSetOrganization(npmOrganization!!, line)
        }
    }
}
