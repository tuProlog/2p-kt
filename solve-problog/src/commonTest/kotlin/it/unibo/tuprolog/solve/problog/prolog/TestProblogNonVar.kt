package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestNonVar
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogNonVar :
    TestNonVar,
    SolverFactory by ProblogSolverFactory {
    private val prototype = TestNonVar.prototype(this)

    @Test
    override fun testNonVarNumber() {
        prototype.testNonVarNumber()
    }

    @Test
    override fun testNonVarFoo() {
        prototype.testNonVarFoo()
    }

    @Test
    override fun testNonVarFooCl() {
        prototype.testNonVarFooCl()
    }

    @Test
    override fun testNonVarFooAssignment() {
        prototype.testNonVarFooAssignment()
    }

    @Test
    override fun testNonVarAnyTerm() {
        prototype.testNonVarAnyTerm()
    }

    @Test
    override fun testNonVar() {
        prototype.testNonVar()
    }
}
