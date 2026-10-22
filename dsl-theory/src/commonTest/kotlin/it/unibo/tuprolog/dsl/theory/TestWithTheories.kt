package it.unibo.tuprolog.dsl.theory

import it.unibo.tuprolog.unify.Unificator
import kotlin.test.Test
import kotlin.test.assertSame
import it.unibo.tuprolog.dsl.unify.lp as unifyLp

class TestWithTheories {
    private val customUnificator = Unificator.naive()

    @Test
    fun withTheoriesPreservesTheReceiverUnificator() {
        val upgraded = unifyLp(customUnificator) { withTheories() }
        assertSame(customUnificator, upgraded.unificator)
    }

    @Test
    fun withTheoriesBuildsTheoriesOnTheReceiverUnificator() {
        val theory = unifyLp(customUnificator) { withTheories() }.theoryOf()
        assertSame(customUnificator, theory.unificator)
    }

    @Test
    fun withTheoriesStillAcceptsAnExplicitUnificator() {
        val other = Unificator.naive()
        val upgraded = unifyLp(customUnificator) { withTheories(other) }
        assertSame(other, upgraded.unificator)
    }
}
