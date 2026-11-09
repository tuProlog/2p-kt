import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest

plugins {
    id(
        libs.plugins.ktMpp.mavenPublish
            .get()
            .pluginId,
    )
}

val jvmStackSize: String = findProperty("jvmStackSize")?.toString() ?: "256m"
val jvmMaxHeapSize: String = findProperty("jvmMaxHeapSize")?.toString() ?: "512m"

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                api(project(":solve"))
                implementation(project(":dsl-theory"))
            }
        }

        commonTest {
            dependencies {
                api(project(":test-solve"))
            }
        }
    }
}

tasks.withType<KotlinJvmTest>().configureEach {
    maxHeapSize = jvmMaxHeapSize
    jvmArgs("-Xss$jvmStackSize")
}

// #1010: the regular JVM tests run on a 256 MB stack, which hides accidental recursion in the engine
val jvmTestSmallStack by tasks.registering(Test::class) {
    description = "Runs DeepExecutionSpec on the JVM with a 1 MB stack."
    group = "verification"
    val jvmTest = tasks.named<KotlinJvmTest>("jvmTest").get()
    testClassesDirs = jvmTest.testClassesDirs
    classpath = jvmTest.classpath
    useJUnitPlatform()
    filter.includeTestsMatching("it.unibo.tuprolog.solve.prolog.DeepExecutionSpec")
    maxHeapSize = jvmMaxHeapSize
    jvmArgs("-Xss1m")
}

tasks.named("check") { dependsOn(jvmTestSmallStack) }
