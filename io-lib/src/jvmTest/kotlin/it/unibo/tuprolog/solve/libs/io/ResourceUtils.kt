package it.unibo.tuprolog.solve.libs.io

actual fun findResource(name: String): Url = UrlSpec::class.java.getResource(name).toUrl()
