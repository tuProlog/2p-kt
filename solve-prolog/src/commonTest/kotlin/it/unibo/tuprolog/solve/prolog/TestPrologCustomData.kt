package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestCustomData
import it.unibo.tuprolog.solve.exception.error.TypeError
import kotlin.test.Test
import kotlin.test.assertIs

class TestPrologCustomData :
    TestCustomData,
    SolverFactory by PrologSolverFactory {
    val prototype = TestCustomData.prototype(this)

    @Test
    override fun testApi() {
        prototype.testApi()
    }

    @Test
    override fun testEphemeralData() {
        prototype.testEphemeralData()
    }

    @Test
    override fun testDurableData() {
        prototype.testDurableData()
    }

    @Test
    override fun testPersistentData() {
        prototype.testPersistentData()
    }

    @Test
    fun nonAtomKeysAreTypeErrors() {
        for (functor in listOf("set_ephemeral", "set_durable", "set_persistent")) {
            val solution = solverWithDefaultBuiltins().solveOnce(Struct.of(functor, Integer.of(1), Integer.of(2)))
            assertIs<TypeError>((solution as? Solution.Halt)?.exception, "$functor: $solution")
        }
    }
}
