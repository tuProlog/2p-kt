package it.unibo.tuprolog.unify.exception

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.exception.TuPrologException

/**
 * Signals that [term1] and [term2] could not be unified, as an exception rather than the [Term]/[Substitution]-level
 * failure sentinel ([it.unibo.tuprolog.core.Substitution.failed]) that [it.unibo.tuprolog.unify.Unificator]'s own
 * operations use to represent unification failure.
 */
open class NoUnifyException(
    private val term1: Term,
    private val term2: Term,
    other: Throwable?,
) : TuPrologException(other) {
    constructor(term1: Term, term2: Term) : this(term1, term2, null)

    override val message: String?
        get() = "Cannot match term `$term1` with term `$term2`"
}
