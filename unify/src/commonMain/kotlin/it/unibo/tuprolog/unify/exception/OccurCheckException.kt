package it.unibo.tuprolog.unify.exception

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var

class OccurCheckException(
    term1: Term,
    term2: Term,
    private val innerVar: Var,
    private val innerTerm: Term,
    other: Throwable?
) : NoUnifyException(term1, term2, other) {

    constructor(term1: Term, term2: Term, innerVar: Var, innerTerm: Term) :
        this(term1, term2, innerVar, innerTerm, null)

    override val message: String
        get() = "${super.message} because variable `$innerVar` occurs in term `$innerTerm`"
}
