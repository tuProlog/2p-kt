package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.library.Runtime
import it.unibo.tuprolog.solve.streams.stdlib.DefaultBuiltins
import kotlin.test.Test
import kotlin.test.assertEquals

class TestDefaultBuiltins {
    @Test
    fun testSelfEquality() {
        assertEquals(DefaultBuiltins, DefaultBuiltins)
    }

    @Test
    fun testItemEquality() {
        assertEquals(Runtime.of(DefaultBuiltins), Runtime.of(DefaultBuiltins))
    }
}
