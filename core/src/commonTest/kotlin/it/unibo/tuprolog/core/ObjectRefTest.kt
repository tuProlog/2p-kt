package it.unibo.tuprolog.core

import it.unibo.tuprolog.utils.box
import it.unibo.tuprolog.utils.identifier
import it.unibo.tuprolog.utils.name
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ObjectRefTest {
    private lateinit var obj: Any
    private lateinit var term: ObjectRef

    @BeforeTest
    fun setUp() {
        obj = StringBuilder("hello")
        term = ObjectRef.of(obj)
    }

    @Test
    fun testCreationFromObject() {
        assertSame(obj, term.value)
    }

    @Test
    fun testTypeTestingProperties() {
        assertTrue(term.isConstant)
        assertTrue(term.isObjectRef)
        assertFalse(term.isNullRef)
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
        val otherTerm = ObjectRef.of(obj)
        assertEquals(term, otherTerm)
        assertNotSame(term, otherTerm)
    }

    @Test
    fun testEqualityBasedOnIdentity() {
        val string = box("hello")
        assertEquals(ObjectRef.of(string), ObjectRef.of(string))
        assertNotEquals(ObjectRef.of(string), ObjectRef.of("HELLO".lowercase()))
    }

    @Test
    fun testToString() {
        val string = term.toString()
        assertTrue(string.startsWith("<"))
        assertTrue(string.endsWith("${StringBuilder::class.name}#${obj.identifier}#$obj>"))
    }

    @Test
    fun testSideEffects() {
        (obj as StringBuilder).reverse()
        testToString()
    }

    @Test
    fun testHashCode() {
        val set = mutableSetOf(term)
        set.add(term)
        assertEquals(1, set.size)
        assertTrue(term in set)
    }
}
