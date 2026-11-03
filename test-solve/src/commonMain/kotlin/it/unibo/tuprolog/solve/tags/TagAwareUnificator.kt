package it.unibo.tuprolog.solve.tags

import it.unibo.tuprolog.unify.AbstractWorklistUnificator
import it.unibo.tuprolog.unify.UnificationStep

/** A worklist [it.unibo.tuprolog.unify.Unificator] rejecting every comparison of terms [verse] does not accept. */
internal class TagAwareUnificator(
    private val verse: TagComparisonVerse,
) : AbstractWorklistUnificator() {
    override fun handleComparison(step: UnificationStep) {
        if (!verse.accepts(step.lhs, step.rhs)) step.reject()
    }
}
