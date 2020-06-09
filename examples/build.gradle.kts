import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val javaVersion: String by project
val ktFreeCompilerArgsJvm: String by project

dependencies {

    api(project(":solve-classic"))
    api(project(":dsl-theory"))
    api(kotlin("stdlib-jdk8"))

    testImplementation(kotlin("test-junit"))
}

configure<JavaPluginConvention> {
    targetCompatibility = JavaVersion.valueOf("VERSION_1_$javaVersion")
    sourceCompatibility = JavaVersion.valueOf("VERSION_1_$javaVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.$javaVersion"
        freeCompilerArgs = ktFreeCompilerArgsJvm.split(";").toList()
    }
}