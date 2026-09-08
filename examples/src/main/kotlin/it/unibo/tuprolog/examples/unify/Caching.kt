package it.unibo.tuprolog.examples.unify

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.Unificator

/**
 * Demonstrates wrapping an `it.unibo.tuprolog.unify.Unificator` with a memoizing decorator via
 * [Unificator.cached][it.unibo.tuprolog.unify.Unificator.cached].
 *
 * The cached unificator behaves exactly like the wrapped one (here,
 * `it.unibo.tuprolog.unify.Unificator.default`) but remembers the outcome of up to `capacity`
 * distinct unification requests, avoiding recomputing [mgu][it.unibo.tuprolog.unify.Unificator.mgu],
 * [match][it.unibo.tuprolog.unify.Unificator.match] and
 * [unify][it.unibo.tuprolog.unify.Unificator.unify] calls for term pairs seen before. This is
 * useful when the same unification is likely to be repeated many times during resolution, at the
 * cost of the extra memory needed for the cache.
 *
 * Running this example prints `{X_0=abraham}`, `true`, and `father(abraham, isaac)`, exactly as
 * the uncached `it.unibo.tuprolog.unify.Unificator.default` would.
 */
fun main() {
    val unificator = Unificator.default
    val cached = Unificator.cached(unificator, capacity = 5)

    val term = Struct.of("father", Atom.of("abraham"), Atom.of("isaac"))
    val template = Struct.of("father", Var.of("X"), Atom.of("isaac"))

    val substitution: Substitution = cached.mgu(term, template)
    val match: Boolean = cached.match(term, template)
    val unified: Term? = cached.unify(term, template)

    println(substitution) // {X_0=abraham}
    println(match) // true
    println(unified) // father(abraham, isaac)
}
