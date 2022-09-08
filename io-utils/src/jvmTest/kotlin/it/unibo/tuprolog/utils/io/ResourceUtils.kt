package it.unibo.tuprolog.utils.io

actual fun findResource(name: String): Url {
    return TestUrl::class.java.getResource(name).toUrl()
}
