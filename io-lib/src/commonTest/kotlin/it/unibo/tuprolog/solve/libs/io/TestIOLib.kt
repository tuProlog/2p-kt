package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.library.Runtime
import kotlin.test.Test
import kotlin.test.assertEquals

class TestIOLib {
    @Test
    fun testSelfEquality() {
        assertEquals(IOLib, IOLib)
    }

    @Test
    fun testItemEquality() {
        assertEquals(Runtime.of(IOLib), Runtime.of(IOLib))
    }
}
