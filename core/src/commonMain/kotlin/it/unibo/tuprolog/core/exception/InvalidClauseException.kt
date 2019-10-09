package it.unibo.tuprolog.core.exception

import it.unibo.tuprolog.core.Term

open class InvalidClauseException(val term: Term, cause: Throwable? = null) : TuPrologException(cause) {
    override val message: String?
        get() = "Term `$term` is not a valid clause"
}
