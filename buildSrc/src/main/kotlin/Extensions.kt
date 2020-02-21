import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.testing.AbstractTestTask
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestResult
import org.gradle.kotlin.dsl.KotlinClosure2
import org.gradle.kotlin.dsl.withType

fun Project.configureTestResultPrinting() {
    tasks.withType<AbstractTestTask> {
        afterSuite(KotlinClosure2({ desc: TestDescriptor, result: TestResult ->
            if (desc.parent == null) { // will match the outermost suite
                println("Results: ${result.resultType} (${result.testCount} tests, ${result.successfulTestCount} successes, ${result.failedTestCount} failures, ${result.skippedTestCount} skipped)")
            }
        }))
    }
}

fun MavenPublication.configurePom(projectName: String) {
    pom {
        name.set("2P in Kotlin -- Module `${projectName}`")
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

fun Project.getPropertyOrWarnForAbsence(key: String): String? {
    val value = property(key)?.toString()
    if (value.isNullOrBlank()) {
        System.err.println("WARNING: $key is not set")
    }
    return value
}

fun capitalize(s: String) = s[0].toUpperCase() + s.substring(1)

val Project.docDir: String
    get() = "$buildDir/doc"