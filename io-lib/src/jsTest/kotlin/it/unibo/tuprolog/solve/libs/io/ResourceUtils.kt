package it.unibo.tuprolog.solve.libs.io

actual fun findResource(name: String): Url =
    Url.file("${js("process.cwd()")}/$name")
