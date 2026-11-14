package it.unibo.tuprolog.solve.search

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Pattern
import it.unibo.tuprolog.solve.ExecutionContext
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

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

    companion object {
        /**
         * Tries the predicates defined in the static and then the dynamic knowledge base, with the arity of the call,
         * once each, in the order they are first defined. Neither library predicates (control constructs included)
         * nor symbols defined only by metarules are tried.
         */
        @JvmStatic
        @JsName("ENUMERATE")
        val ENUMERATE: MetaResolutionPolicy =
            MetaResolutionPolicy { goal, context ->
                // shortcut: scans the clauses at every such call, fine while enumeration is rare; index symbols if not
                (context.staticKb.clauses.asSequence() + context.dynamicKb.clauses.asSequence())
                    .mapNotNull { it.head?.asStruct() }
                    .filter { it.arity == goal.arity }
                    .map { it.functor }
                    .distinct()
                    .map { Atom.of(it) }
                    .toList()
                    .asSequence()
            }
    }
}
