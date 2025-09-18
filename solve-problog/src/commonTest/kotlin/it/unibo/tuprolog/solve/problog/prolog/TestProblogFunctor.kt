package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestFunctor
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogFunctor :
    TestFunctor,
    SolverFactory by ProblogSolverFactory {
    private val prototype = TestFunctor.prototype(this)

    @Test
    override fun testFunArity() {
        prototype.testFunArity()
    }

    @Test
    override fun testFunArityWithSub() {
        prototype.testFunArityWithSub()
    }

    @Test
    override fun testFunMats() {
        prototype.testFunMats()
    }

    @Test
    override fun testFunArityZero() {
        prototype.testFunArityZero()
    }

    @Test
    override fun testFunWrongArity() {
        prototype.testFunWrongArity()
    }

    @Test
    override fun testFunWrongName() {
        prototype.testFunWrongName()
    }

    @Test
    override fun testFunXNameYArity() {
        prototype.testFunXNameYArity()
    }

    @Test
    override fun testFunDecNum() {
        prototype.testFunDecNum()
    }

    @Test
    override fun testFunConsOf() {
        prototype.testFunConsOf()
    }

    @Test
    override fun testFunEmptyList() {
        prototype.testFunEmptyList()
    }

    @Test
    override fun testFunXYWrongArity() {
        prototype.testFunXYWrongArity()
    }

    @Test
    override fun testFunXNArity() {
        prototype.testFunXNArity()
    }

    @Test
    override fun testFunXAArity() {
        prototype.testFunXAArity()
    }

    @Test
    override fun testFunNumName() {
        prototype.testFunNumName()
    }

    @Test
    override fun testFunFooName() {
        prototype.testFunFooName()
    }

    @Test
    override fun testFunFlag() {
        prototype.testFunFlag()
    }

    @Test
    override fun testFunNegativeArity() {
        prototype.testFunNegativeArity()
    }
}
