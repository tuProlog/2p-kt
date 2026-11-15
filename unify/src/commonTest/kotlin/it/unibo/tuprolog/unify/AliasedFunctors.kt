package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Term

/** A unification of `P(a)` with `Q(a)`, then bindings of their functor variables, which must yield [expected]. */
internal class AliasedFunctors(
    val id: String,
    val then: PatternVars.() -> Pair<Term, Term>,
    // what P must become, or `null` if the bindings must be rejected
    val expected: Term?,
)
