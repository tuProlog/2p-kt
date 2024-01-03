package it.unibo.tuprolog.core.exception

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import kotlin.jvm.JvmOverloads

class SubstitutionApplicationException : SubstitutionException {
    val term: Term

    @JvmOverloads
    constructor(term: Term, substitution: Substitution, message: String?, cause: Throwable? = null) : super(
        substitution,
        message,
        cause,
    ) {
        this.term = term
    }

    @JvmOverloads
    constructor(term: Term, substitution: Substitution, cause: Throwable? = null) :
        this(
            term,
            substitution,
            "Could not apply $substitution to $term",
            cause,
        )

    @JvmOverloads
    constructor(term: Term, cause: Throwable? = null) :
        this(
            term,
            Substitution.failed(),
            cause,
        )
}
