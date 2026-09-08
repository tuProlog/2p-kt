package it.unibo.tuprolog.examples.unify

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.AbstractUnificator

/**
 * Demonstrates defining a custom unification strategy by extending
 * `it.unibo.tuprolog.unify.AbstractUnificator` and overriding
 * [AbstractUnificator.checkTermsEquality][it.unibo.tuprolog.unify.AbstractUnificator.checkTermsEquality],
 * the hook used to decide whether two non-variable, non-compound terms are considered equal.
 *
 * The custom unificator treats two numeric terms as equal whenever their absolute values match,
 * falling back to plain equality (`==`) for every other kind of term. As a result,
 * `f(1)` and `f(-1)` are found to [match][it.unibo.tuprolog.unify.Unificator.match] even though
 * `1` and `-1` are not structurally equal, showing how `AbstractUnificator` can be extended to
 * implement domain-specific notions of "sameness" beyond standard Prolog unification.
 *
 * Running this example prints `true` (the terms match) and `f(1)` (the unified term, using the
 * value carried by the first argument).
 *
 * @author Lorenzo
 */
fun main() {
    Scope.empty {
        val unificator =
            object : AbstractUnificator() {
                override fun checkTermsEquality(
                    first: Term,
                    second: Term,
                ): Boolean =
                    when {
                        first is Integer && second is Integer ->
                            first.value.absoluteValue.compareTo(second.value.absoluteValue) == 0
                        first is Numeric && second is Numeric ->
                            first.decimalValue.absoluteValue.compareTo(second.decimalValue.absoluteValue) == 0
                        else -> first == second
                    }
            }

        val term1 = structOf("f", numOf("1"))
        val term2 = structOf("f", numOf("-1"))

        val match = unificator.match(term1, term2) // true

        val result = unificator.unify(term1, term2) // f(1)

        println(match)
        println(result)
    }
}
