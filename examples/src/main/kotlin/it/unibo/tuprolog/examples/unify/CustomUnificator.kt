package it.unibo.tuprolog.examples.unify

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.AbstractUnificator

/**
 *
 *
 * @author Lorenzo
 */
fun main() {
    Scope.empty {
        val unificator = object : AbstractUnificator() {
            override fun checkTermsEquality(first: Term, second: Term): Boolean = when {
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
