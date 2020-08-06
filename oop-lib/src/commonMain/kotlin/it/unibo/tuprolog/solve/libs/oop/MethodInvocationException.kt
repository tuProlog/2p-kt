package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.core.exception.TuPrologException

class MethodInvocationException : TuPrologException {
    constructor(ref: ObjectRef, missingMethodName: String) :
            super("Missing method `$missingMethodName` on term $ref referencing object ${ref.`object`}")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}