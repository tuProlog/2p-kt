package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Term

internal class UnifiableCase(
    val id: String,
    val terms: PatternVars.() -> Pair<Term, Term>,
)
