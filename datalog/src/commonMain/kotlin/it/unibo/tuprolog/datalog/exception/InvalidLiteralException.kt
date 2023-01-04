package it.unibo.tuprolog.datalog.exception

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.exception.TuPrologException

@Suppress("MemberVisibilityCanBePrivate")
class InvalidLiteralException : TuPrologException {
    val literal: Term

    val clause: Clause?

    constructor(literal: Term, clause: Clause?, cause: Throwable? = null) : super(
        message = "Invalid literal in clause${clause?.let { " $it" } ?: ""}: $literal",
        cause = cause
    ) {
        this.literal = literal
        this.clause = clause
    }

    constructor(literal: Term, cause: Throwable? = null) : this(literal, null, cause)
}
