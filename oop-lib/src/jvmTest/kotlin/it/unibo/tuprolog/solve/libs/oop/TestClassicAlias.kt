package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.classic.ClassicSolverFactory
import org.junit.Test

class TestClassicAlias :
    TestAlias,
    SolverFactory by ClassicSolverFactory {
    private val prototype = TestAlias.prototype(this)

    @Test
    override fun testDefaultAliases() {
        prototype.testDefaultAliases()
    }

    @Test
    override fun testAliasIsBacktrackable() {
        prototype.testAliasIsBacktrackable()
    }

    @Test
    override fun testRegisterAndAlias() {
        prototype.testRegisterAndAlias()
    }

    @Test
    override fun testRegisterWithWrongArguments() {
        prototype.testRegisterWithWrongArguments()
    }
}
