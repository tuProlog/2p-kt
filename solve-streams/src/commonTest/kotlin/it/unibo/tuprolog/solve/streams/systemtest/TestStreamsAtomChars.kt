package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestAtomChars
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsAtomChars :
    TestAtomChars,
    SolverFactory by StreamsSolverFactory {
    private val prototype = TestAtomChars.prototype(this)

    @Test
    override fun atomCharsTestFirstIsVar() {
        prototype.atomCharsTestFirstIsVar()
    }

    @Test
    override fun atomCharsTestYes() {
        prototype.atomCharsTestYes()
    }

    @Test
    override fun atomCharsTestOneCharIsVar() {
        prototype.atomCharsTestOneCharIsVar()
    }

    @Test
    override fun atomCharsTestFailure() {
        prototype.atomCharsTestFailure()
    }

    @Test
    override fun atomCharsTestEmpty() {
        prototype.atomCharsTestEmpty()
    }

    @Test
    override fun atomCharsTestListHead() {
        prototype.atomCharsTestListHead()
    }

    @Test
    @Ignore
    override fun atomCharsTestIstantationErrorCheck() {
        prototype.atomCharsTestIstantationErrorCheck()
    }

    @Test
    @Ignore
    override fun atomCharsTestTypeErrorCheck() {
        prototype.atomCharsTestTypeErrorCheck()
    }
}
