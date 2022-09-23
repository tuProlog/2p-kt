package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.problog.lib.ProblogLib
import kotlin.test.Test
import kotlin.test.assertEquals

class TestDefaultBuiltins {
    @Test
    fun testSelfEquality() {
        assertEquals(ProblogLib, ProblogLib)
    }

    @Test
    fun testItemEquality() {
        assertEquals(Runtime.of(ProblogLib), Runtime.of(ProblogLib))
    }
}
