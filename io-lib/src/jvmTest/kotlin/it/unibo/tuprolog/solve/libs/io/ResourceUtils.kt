package it.unibo.tuprolog.solve.libs.io

actual fun findResource(name: String): Url {
    return TestUrl::class.java.getResource(name).toUrl()
}
