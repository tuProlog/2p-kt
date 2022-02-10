import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `kotlin-jvm-js`
    alias(libs.plugins.shadowJar)
    `kotlin-doc`
    `publish-on-maven`
    `publish-on-npm`
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                subprojects(ktProjects, except="test-solve") {
                    api(project(path))
                }
            }

        }
        val jvmMain by getting {
            dependencies {
                subprojects(jvmProjects, except="examples") {
                    api(project(path))
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
