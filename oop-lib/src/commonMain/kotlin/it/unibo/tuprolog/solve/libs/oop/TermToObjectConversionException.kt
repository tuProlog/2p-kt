package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.exception.TuPrologException
import kotlin.reflect.KClass

class TermToObjectConversionException : TuPrologException {
    constructor(term: Term, type: KClass<*>) : super("Term `$term` cannot be converted into an object of type ${type.qualifiedName}")
    constructor(term: Term) : super("Term `$term` cannot be converted into ab object")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}