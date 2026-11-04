package it.unibo.tuprolog.unify.impl

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.UnificationRequest
import it.unibo.tuprolog.unify.UnificationStep

/** The [UnificationStep] reused for every pair of one unification run. */
internal class MutableUnificationStep(
    override val request: UnificationRequest,
) : UnificationStep {
    override lateinit var lhs: Term
        private set

    override lateinit var rhs: Term
        private set

    var verdict: StepVerdict = StepVerdict.PROCEED
        private set

    fun reset(
        lhs: Term,
        rhs: Term,
    ) {
        this.lhs = lhs
        this.rhs = rhs
        verdict = StepVerdict.PROCEED
    }

    override fun replace(
        lhs: Term,
        rhs: Term,
    ) {
        this.lhs = lhs
        this.rhs = rhs
        verdict = StepVerdict.REPLACED
    }

    override fun accept() {
        verdict = StepVerdict.ACCEPTED
    }

    override fun reject() {
        verdict = StepVerdict.REJECTED
    }
}
