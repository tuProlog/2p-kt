package it.unibo.tuprolog.solve.tags

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.AbstractWorklistUnificator
import it.unibo.tuprolog.unify.BindingStore
import it.unibo.tuprolog.unify.IncrementalUnificator
import it.unibo.tuprolog.unify.UnificationStep

/** A worklist unificator opting into [IncrementalUnificator], with [onComparison] as its comparison hook. */
private class IncrementalTagUnificator(
    private val onComparison: (UnificationStep) -> Unit,
) : AbstractWorklistUnificator(),
    IncrementalUnificator {
    override fun handleComparison(step: UnificationStep) = onComparison(step)

    override fun unifyInto(
        store: BindingStore,
        term1: Term,
        term2: Term,
        occurCheckEnabled: Boolean,
    ) = unifyIncrementally(store, term1, term2, occurCheckEnabled)
}

/** [TagAwareUnificator], unifying in the solver's bindings. */
internal fun incrementalTagAwareUnificator(verse: TagComparisonVerse): IncrementalUnificator =
    IncrementalTagUnificator { step -> if (!verse.accepts(step.lhs, step.rhs)) step.reject() }

/** [TagFlowUnificator], unifying in the solver's bindings. */
internal fun incrementalTagFlowUnificator(flow: TagFlow): IncrementalUnificator =
    IncrementalTagUnificator { step -> flow.observe(step.lhs, step.rhs) }
