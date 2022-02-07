import org.gradle.api.Project

val otherProjects = setOf("documentation")

val jsProjects = setOf("parser-js")

val jvmProjects = setOf("examples", "ide", "ide-plp")

val Project.ktProjects
    get() = rootProject.allprojects.except(otherProjects + jsProjects + jvmProjects).map { it.name }
