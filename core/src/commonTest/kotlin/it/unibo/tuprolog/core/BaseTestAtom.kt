package it.unibo.tuprolog.core

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

abstract class BaseTestAtom {

    abstract val atomsUnderTest: Array<String>

    @Test
    fun atomCreation() {
        atomsUnderTest.forEach { value ->
            val reference = Atom.of(value)

            listOf(Atom.of(value), Struct.of(value)).forEach {
                assertEquals(reference, it)
                assertTrue(reference == it)
                assertTrue(it == reference)
            }
        }
    }

    @Test
    fun atomType() {
        atomsUnderTest.forEach { value ->

            listOf(Atom.of(value), Struct.of(value)).forEach {
                assertTrue(it is Atom)
                assertTrue(it is Struct)
                assertTrue(it is Term)

                assertFalse(it is Numeric)
                assertFalse(it is Clause)
                assertFalse(it is Var)
                assertFalse(it is Couple)
            }
        }
    }

    @Test
    fun atomValueAndFunctor() {
        atomsUnderTest.forEach { value ->

            listOf(Atom.of(value), Struct.of(value)).forEach {
                assertTrue(it is Atom)
                assertEquals(value, it.value)
                assertEquals(value, it.functor)
            }
        }
    }

    @Test
    fun atomZeroArity() {
        atomsUnderTest.forEach { value ->
            listOf(Atom.of(value), Struct.of(value)).forEach {
                assertTrue(it is Atom)
                assertEquals(0, it.arity)
                assertTrue(it.args.contentDeepEquals(emptyArray()))
                assertEquals(emptyList(), it.argsList)
                assertEquals(emptySequence(), it.argsSequence)
            }
        }
    }

    @Test
    fun atomTests() {
        atomsUnderTest.forEach { value ->

            listOf(Atom.of(value), Struct.of(value)).forEach {
                assertTrue(it.isAtom)
                assertTrue(it.isStruct)

                assertTrue(it is Atom)
                assertTrue(it is Struct)

                assertFalse(it.isNumber)
                assertFalse(it.isReal)
                assertFalse(it.isInt)
                assertFalse(it.isClause)
                assertFalse(it.isDirective)
                assertFalse(it.isFact)
                assertFalse(it.isRule)
                assertFalse(it.isCouple)

                assertFalse(it is Numeric)
                assertFalse(it is Clause)
                assertFalse(it is Var)
            }
        }
    }
}