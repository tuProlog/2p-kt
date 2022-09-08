package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.utils.io.Url
import it.unibo.tuprolog.utils.io.exceptions.IOException
import it.unibo.tuprolog.utils.io.toUrl

actual fun findResource(name: String): Url =
    TestInclude::class.java.getResource(name)?.toUrl() ?: throw IOException("No such a resource: $name")
