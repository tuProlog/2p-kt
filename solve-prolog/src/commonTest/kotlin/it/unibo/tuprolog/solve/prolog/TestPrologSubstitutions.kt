package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestSubstitutions
import kotlin.test.Test

class TestPrologSubstitutions :
    TestSubstitutions,
    SolverFactory by PrologSolverFactory {
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
