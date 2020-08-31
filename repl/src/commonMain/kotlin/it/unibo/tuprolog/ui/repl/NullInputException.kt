package it.unibo.tuprolog.ui.repl

import it.unibo.tuprolog.core.exception.TuPrologException

class NullInputException : TuPrologException {
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?) : this(message, null)
}