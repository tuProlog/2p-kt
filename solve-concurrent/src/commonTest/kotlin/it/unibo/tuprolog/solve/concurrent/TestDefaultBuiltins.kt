package it.unibo.tuprolog.solve.concurrent

import it.unibo.tuprolog.solve.concurrent.stdlib.DefaultBuiltins
import it.unibo.tuprolog.solve.library.Runtime
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
