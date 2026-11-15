package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var

/** A unification, and the bindings it must yield (`null` if it must fail). */
internal class MetaUnification(
    val id: String,
    val terms: PatternVars.() -> Pair<Term, Term>,
    val bindings: (PatternVars.() -> Map<Var, Term>)?,
)
