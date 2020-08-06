package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.exception.TuPrologException

class TermToObjectConversionException : TuPrologException {
    constructor(term: Term) : super("Term `$term` cannot be converted to object")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}