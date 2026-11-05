package it.unibo.tuprolog.examples.benchmarks.theory

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.Unificator

/**
 * A [Unificator] counting its `match` calls (how many candidates a theory actually tried to unify) and its `mgu` calls
 * (how many unifiers were computed, e.g. by a solver's resolution steps).
 */
class CountingUnificator(
    private val delegate: Unificator = Unificator.default,
) : Unificator by delegate {
    var matches: Long = 0
        private set

    var mgus: Long = 0
        private set

    override fun mgu(
        term1: Term,
        term2: Term,
        occurCheckEnabled: Boolean,
    ): Substitution {
        mgus++
        return delegate.mgu(term1, term2, occurCheckEnabled)
    }

    override fun mgu(
        term1: Term,
        term2: Term,
    ): Substitution = mgu(term1, term2, true)

    override fun match(
        term1: Term,
        term2: Term,
        occurCheckEnabled: Boolean,
    ): Boolean {
        matches++
        return delegate.match(term1, term2, occurCheckEnabled)
    }

    override fun match(
        term1: Term,
        term2: Term,
    ): Boolean = match(term1, term2, true)

    fun reset() {
        matches = 0
        mgus = 0
    }
}
