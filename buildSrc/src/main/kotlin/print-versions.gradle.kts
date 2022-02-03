val versioning = "versioning"

tasks.register("printVersion") {
    group = versioning
    doLast { println(project.version) }
}

tasks.register("printNpmVersion") {
    group = versioning
    doLast { println(project.npmCompliantVersion) }
}
