package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestSubstitutions
import kotlin.test.Test

class TestClassicSubstitutions : TestSubstitutions, SolverFactory by ClassicSolverFactory {

    private val prototype = TestSubstitutions.prototype(this)

    @Test
    override fun interestingVariablesAreNotObliterated() {
        prototype.interestingVariablesAreNotObliterated()
    }

    @Test
    override fun interestingVariablesAreProperlyTracked() {
        prototype.interestingVariablesAreProperlyTracked()
    }

    @Test
    override fun uninterestingVariablesAreObliterated() {
        prototype.uninterestingVariablesAreObliterated()
    }
}
