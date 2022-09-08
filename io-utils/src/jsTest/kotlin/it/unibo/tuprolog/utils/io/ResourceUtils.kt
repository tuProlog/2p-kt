package it.unibo.tuprolog.utils.io

actual fun findResource(name: String): Url =
    Url.file("${js("process.cwd()")}/$name")
