package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestFunctor
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsFunctor :
    TestFunctor,
    SolverFactory by StreamsSolverFactory {
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
    @Ignore
    override fun testFunXNameYArity() {
        prototype.testFunXNameYArity()
    }

    @Test
    @Ignore
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
    @Ignore
    override fun testFunXYWrongArity() {
        prototype.testFunXYWrongArity()
    }

    @Test
    @Ignore
    override fun testFunXNArity() {
        prototype.testFunXNArity()
    }

    @Test
    @Ignore
    override fun testFunXAArity() {
        prototype.testFunXAArity()
    }

    @Test
    @Ignore
    override fun testFunNumName() {
        prototype.testFunNumName()
    }

    @Test
    @Ignore
    override fun testFunFooName() {
        prototype.testFunFooName()
    }

    @Test
    @Ignore
    override fun testFunFlag() {
        prototype.testFunFlag()
    }

    @Test
    @Ignore
    override fun testFunNegativeArity() {
        prototype.testFunNegativeArity()
    }
}
