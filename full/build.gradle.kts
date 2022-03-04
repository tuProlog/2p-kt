import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `kotlin-mp`
    `kotlin-doc`
    `publish-on-maven`
    `publish-on-npm`
    alias(libs.plugins.shadowJar)
}

val thisProject = project.name

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                subprojects(ktProjects, except = setOf("test-solve", thisProject)) {
                    api(this)
                }
            }
        }
        val jvmMain by getting {
            dependencies {
                subprojects(jvmProjects, except = "examples") {
                    api(this)
                }
            }
        }
    }
}

val shadowJar by tasks.getting(ShadowJar::class) {
    dependsOn("jvmMainClasses")
    archiveBaseName.set(rootProject.name)
    archiveClassifier.set("full")
    from(kotlin.jvm().compilations.getByName("main").output)
    from(files("${rootProject.projectDir}/LICENSE"))
}
