@file:Suppress("DEPRECATION")

package it.unibo.tuprolog.solve.tags

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.AbstractUnificator
import it.unibo.tuprolog.unify.Equation

/** An equation-based [it.unibo.tuprolog.unify.Unificator] reporting every equation it handles to [flow]. */
internal class EquationalTagFlowUnificator(
    private val flow: TagFlow,
) : AbstractUnificator() {
    override fun checkTermsEquality(
        first: Term,
        second: Term,
    ): Boolean = first == second

    override fun handleEquation(
        request: Request,
        equation: Equation,
    ): Equation = equation.also { flow.observe(it.lhs, it.rhs) }
}
