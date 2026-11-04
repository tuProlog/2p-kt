package it.unibo.tuprolog.solve.tags

import it.unibo.tuprolog.unify.AbstractWorklistUnificator
import it.unibo.tuprolog.unify.UnificationStep

/** A worklist [it.unibo.tuprolog.unify.Unificator] reporting every comparison to [flow]. */
internal class TagFlowUnificator(
    private val flow: TagFlow,
) : AbstractWorklistUnificator() {
    override fun handleComparison(step: UnificationStep) = flow.observe(step.lhs, step.rhs)
}
