package it.unibo.tuprolog.solve.libs.io

actual fun findResource(name: String): Url =
    Url.file("${js("process.cwd()")}/kotlin/it/unibo/tuprolog/solve/libs/io/$name")

actual fun env(name: String): String? = js("process.env[name]") as String?
