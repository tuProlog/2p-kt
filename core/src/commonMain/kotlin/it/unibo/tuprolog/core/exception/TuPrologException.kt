package it.unibo.tuprolog.core.exception

/**
 * Base class for all `tuProlog` related exceptions
 */
open class TuPrologException : RuntimeException {
    constructor(message: String?, cause: Throwable?) : super(message, cause)

    constructor(message: String?) : super(message)

    constructor(cause: Throwable?) : this(cause?.toString(), cause)

    constructor() : super()
}
