package it.unibo.tuprolog.unify.exception

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.exception.TuPrologException

open class NoUnifyException(
    private val term1: Term,
    private val term2: Term,
    other: Throwable?,
) : TuPrologException(other) {
    constructor(term1: Term, term2: Term) : this(term1, term2, null)

    override val message: String?
        get() = "Cannot match term `$term1` with term `$term2`"
}
