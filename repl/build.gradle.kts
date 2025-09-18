plugins {
    id(
        libs.plugins.ktMpp.mavenPublish
            .get()
            .pluginId,
    )
    id(
        libs.plugins.ktMpp.fatJar
            .get()
            .pluginId,
    )
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(libs.clikt)
                api(project(":core"))
                api(project(":oop-lib"))
                api(project(":io-lib"))
                api(project(":solve-classic"))
                api(project(":parser-theory"))
            }
        }
    }
}

tasks.register("run", JavaExec::class.java) {
    group = "application"
    dependsOn(tasks.named("jvmMainClasses"))
    classpath =
        files(
            kotlin
                .jvm()
                .compilations
                .getByName("main")
                .output,
            kotlin
                .jvm()
                .compilations
                .getByName("main")
                .compileDependencyFiles,
        )
    standardInput = System.`in`
    mainClass.set(multiPlatformHelper.fatJarEntryPoint)
    project.findProperty("arguments")?.let {
        args = it.toString().split("\\s+".toRegex()).filterNot(String::isBlank)
    }
}
