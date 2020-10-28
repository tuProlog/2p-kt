package it.unibo.tuprolog.solve.libs.io.exceptions

import it.unibo.tuprolog.core.exception.TuPrologException

class InvalidUrlException : TuPrologException {
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}
