@file:Suppress("DEPRECATION")

package it.unibo.tuprolog.solve.tags

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.AbstractUnificator
import it.unibo.tuprolog.unify.Equation

/** An equation-based [it.unibo.tuprolog.unify.Unificator] turning every equation [verse] does not accept into a contradiction. */
internal class EquationalTagAwareUnificator(
    private val verse: TagComparisonVerse,
) : AbstractUnificator() {
    override fun checkTermsEquality(
        first: Term,
        second: Term,
    ): Boolean = first == second

    override fun handleEquation(
        request: Request,
        equation: Equation,
    ): Equation =
        when {
            equation.isContradiction || verse.accepts(equation.lhs, equation.rhs) -> equation
            else -> equation.toContradiction()
        }
}
