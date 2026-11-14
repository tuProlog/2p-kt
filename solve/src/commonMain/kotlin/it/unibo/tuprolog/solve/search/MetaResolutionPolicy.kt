package it.unibo.tuprolog.solve.search

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Pattern
import it.unibo.tuprolog.solve.ExecutionContext
import kotlin.js.JsName

/**
 * Chooses the predicate symbols to try for a call whose functor is an unbound variable, e.g. `Q(a)` in the body of the
 * metarule `P(X) :- Q(X)`, when passed via [it.unibo.tuprolog.solve.SolveOptions.setMetaResolution]. Without a policy
 * (the default), such calls unify with the heads of the clauses of their arity, binding their functors, whereas calls
 * whose functor is bound run as usual.
 *
 * Each candidate is bound to the functor of the call in turn, on backtracking, and the call then runs as an ordinary
 * one: a candidate without clauses fails (or raises an existence error, as the `unknown` flag says), and the search may
 * not terminate if the candidates call themselves back.
 */
fun interface MetaResolutionPolicy {
    /** The functors to try, in order, for [goal], whose functor is unbound. */
    @JsName("candidates")
    fun candidates(
        goal: Pattern,
        context: ExecutionContext,
    ): Sequence<Atom>
}
