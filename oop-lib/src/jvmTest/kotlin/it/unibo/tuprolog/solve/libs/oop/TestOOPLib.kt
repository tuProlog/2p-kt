package it.unibo.tuprolog.solve.libs.oop

import it.unibo.tuprolog.solve.library.Runtime
import kotlin.test.Test
import kotlin.test.assertEquals

class TestOOPLib {
    @Test
    fun testSelfEquality() {
        assertEquals(OOPLib, OOPLib)
    }

    @Test
    fun testItemEquality() {
        assertEquals(Runtime.of(OOPLib), Runtime.of(OOPLib))
    }
}
