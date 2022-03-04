import org.gradle.api.Project

val otherProjects = setOf("documentation")

val jsProjects = setOf("parser-js")

val jvmProjects = setOf("examples", "ide", "ide-plp", "parser-jvm")

val Project.ktProjects
    get() = rootProject.subprojects.except(otherProjects + jsProjects + jvmProjects).map { it.name }
