package it.unibo.tuprolog.core

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class NullRefTest {
    private lateinit var term: NullRef

    @BeforeTest
    fun setUp() {
        term = NullRef()
    }

    @Test
    fun testCannotAccessValue() {
        assertFailsWith<NullPointerException> {
            term.value
        }
    }

    @Test
    fun testTypeTestingProperties() {
        assertTrue(term.isConstant)
        assertTrue(term.isObjectRef)
        assertTrue(term.isNullRef)
        assertFalse(term.isAtom)
        assertFalse(term.isStruct)
        assertFalse(term.isVar)
    }

    @Test
    fun testCopy() {
        assertSame(term, term.freshCopy())
    }

    @Test
    fun testEquality() {
        assertEquals(term, ObjectRef.of(null))
    }

    @Test
    fun testToString() {
        assertEquals("<null>", term.toString())
    }

    @Test
    fun testHashCode() {
        val set = mutableSetOf(term)
        set.add(term)
        assertEquals(1, set.size)
        assertTrue(term in set)
    }
}
