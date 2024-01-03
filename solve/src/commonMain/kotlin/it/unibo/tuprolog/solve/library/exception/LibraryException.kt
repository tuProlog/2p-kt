package it.unibo.tuprolog.solve.library.exception

import it.unibo.tuprolog.core.exception.TuPrologException

open class LibraryException(
    message: String? = null,
    cause: Throwable? = null,
) : TuPrologException(message, cause) {
    constructor(cause: Throwable?) : this(cause?.toString(), cause)
}
