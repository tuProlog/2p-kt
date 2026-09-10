package it.unibo.tuprolog.datalog.exception

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.exception.TuPrologException

/**
 * Thrown by [it.unibo.tuprolog.datalog.asLiteral] when a [Term] cannot be interpreted as a Prolog/Datalog
 * literal (a callable goal): only a [it.unibo.tuprolog.core.Struct] or a [it.unibo.tuprolog.core.Var] can
 * occur as a literal (the latter is wrapped into a `call/1` goal), so e.g. a bare number or list argument
 * found where a body goal or head was expected triggers this exception.
 *
 * @param literal the offending [Term].
 * @param clause the [Clause] the offending [literal] was found in, if known.
 */
@Suppress("MemberVisibilityCanBePrivate")
class InvalidLiteralException : TuPrologException {
    val literal: Term

    val clause: Clause?

    constructor(literal: Term, clause: Clause?, cause: Throwable? = null) : super(
        message = "Invalid literal in clause${clause?.let { " $it" } ?: ""}: $literal",
        cause = cause,
    ) {
        this.literal = literal
        this.clause = clause
    }

    /** Shorthand for `InvalidLiteralException(literal, null, cause)`, when no owning [Clause] is known. */
    constructor(literal: Term, cause: Throwable? = null) : this(literal, null, cause)
}
