package it.unibo.tuprolog.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestAtom {

    @Test
    fun atomCreation() {
        val value = "anAtom"

        val reference = Atom.of(value)

        listOf(atomOf(value), Struct.of(value), value.toTerm()).forEach {
            assertEquals(reference, it)
            assertTrue(reference == it)
            assertTrue(it == reference)
        }
    }
}