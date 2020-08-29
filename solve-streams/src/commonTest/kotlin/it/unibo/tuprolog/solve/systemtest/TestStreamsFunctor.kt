package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.*
import it.unibo.tuprolog.solve.channel.InputChannel
import it.unibo.tuprolog.solve.channel.OutputChannel
import it.unibo.tuprolog.solve.exception.PrologWarning
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.Theory
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsFunctor : TestFunctor, SolverFactory by StreamsSolverFactory  {
    private val prototype = TestFunctor.prototype(this)

    @Test
    override fun testFunArity(){
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
    @Ignore
    override fun testFunNegativeArity() {
        prototype.testFunNegativeArity()
    }
}