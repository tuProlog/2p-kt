plugins {
    id(
        libs.plugins.ktMpp.mavenPublish
            .get()
            .pluginId,
    )
    alias(libs.plugins.kotlin.allopen)
    alias(libs.plugins.kotlinx.benchmark)
}

// Timing benchmarks (#1012) live in their own source set, so they are neither published nor part of `test`.
val benchmarks: SourceSet =
    sourceSets.create("benchmarks") {
        compileClasspath += sourceSets.main.get().output + sourceSets.main.get().compileClasspath
        runtimeClasspath += sourceSets.main.get().output + sourceSets.main.get().runtimeClasspath
    }

sourceSets.test {
    compileClasspath += benchmarks.output + benchmarks.compileClasspath
    runtimeClasspath += benchmarks.output + benchmarks.runtimeClasspath
}

dependencies {
    api(project(":solve-prolog"))
    api(project(":solve-concurrent"))
    api(project(":solve-problog"))
    api(project(":dsl-theory"))
    api(project(":parser-theory"))
    api(project(":io-lib"))
    api(project(":oop-lib"))
    api(kotlin("stdlib-jdk8"))
    "benchmarksImplementation"(libs.kotlinx.benchmark.runtime)
}

// JMH needs @State classes to be open.
allOpen {
    annotation("org.openjdk.jmh.annotations.State")
}

benchmark {
    targets {
        register("benchmarks")
    }
    configurations {
        // Quick smoke run, without profiling: `./gradlew :examples:benchmarksSmokeBenchmark`
        register("smoke") {
            warmups = 1
            iterations = 1
            iterationTime = 1
            iterationTimeUnit = "s"
            outputTimeUnit = "us"
            mode = "avgt"
            reportFormat = "json"
        }
    }
}

// The full run (#1012), with JMH's GC profiler for allocations, which kotlinx-benchmark's configurations can't enable:
// `./gradlew :examples:timingBenchmarks`, results in build/reports/benchmarks/jmh.json. `-PjmhArgs='<JMH CLI args>'`
// replaces the default run parameters (e.g. `-PjmhArgs='-wi 0 -i 1 -r 100ms TimeoutOvershoot'` for a quick check).
tasks.register<JavaExec>("timingBenchmarks") {
    group = "benchmark"
    description = "Runs the resolution-timing benchmarks with JMH's GC profiler, writing JSON results."
    dependsOn("benchmarksBenchmarkCompile")
    val generated = layout.buildDirectory.dir("benchmarks/benchmarks")
    classpath =
        files(generated.map { it.dir("classes") }, generated.map { it.dir("resources") }) +
        benchmarks.output +
        benchmarks.runtimeClasspath
    mainClass.set("org.openjdk.jmh.Main")
    val reportFile = layout.buildDirectory.file("reports/benchmarks/jmh.json").map { it.asFile }
    outputs.file(reportFile)
    outputs.upToDateWhen { false }
    doFirst { reportFile.get().parentFile.mkdirs() }
    val runParameters =
        (findProperty("jmhArgs") as String?)?.split(" ")?.filter { it.isNotBlank() }
            ?: listOf("-wi", "3", "-i", "5", "-w", "2s", "-r", "2s", "-f", "1")
    args(runParameters + listOf("-prof", "gc", "-rf", "json", "-rff", reportFile.get().absolutePath))
}

// Deterministic clock-read counts per workload (#1012), printed as a Markdown table.
tasks.register<JavaExec>("clockReadsReport") {
    group = "benchmark"
    description = "Prints how many resolution steps and clock reads each benchmark workload takes."
    classpath = benchmarks.runtimeClasspath + benchmarks.output
    mainClass.set("it.unibo.tuprolog.examples.benchmarks.ClockReadsReport")
}
