package it.unibo.tuprolog.solve.libs.io

actual fun findResource(name: String): Url {
    return Url.file("./$name")
}
