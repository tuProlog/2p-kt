package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Term

/** Two consecutive unifications: the second one, in the context of the first's result, must fail. */
internal class DelayedCase(
    val id: String,
    val first: PatternVars.() -> Pair<Term, Term>,
    val second: PatternVars.() -> Pair<Term, Term>,
)
