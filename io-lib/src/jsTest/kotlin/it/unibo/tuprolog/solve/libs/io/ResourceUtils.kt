package it.unibo.tuprolog.solve.libs.io

actual fun findResource(name: String): Url = Url.file("${js("process.cwd()")}/kotlin/it/unibo/tuprolog/solve/libs/io/$name")
