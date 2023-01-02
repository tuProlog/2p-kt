package it.unibo.tuprolog.core.exception

import kotlin.jvm.JvmOverloads

/**
 * Base class for all `tuProlog` related exceptions
 *
 * @param message the detail message string.
 * @param cause the cause of this exception.
 */
open class TuPrologException : RuntimeException {
    constructor(message: String?, cause: Throwable?) : super(message, cause)

    constructor(message: String?) : super(message)

    constructor(cause: Throwable?) : this(cause?.toString(), cause)

    constructor() : super()
}
