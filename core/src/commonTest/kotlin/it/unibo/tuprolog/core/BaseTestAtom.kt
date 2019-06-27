package it.unibo.tuprolog.core

import kotlin.test.*

abstract class BaseTestAtom {

    val atomicPattern = """^[a-z][a-zA-Z0-9_]*$""".toRegex()

    abstract val atomsUnderTest: Array<String>

    @Test
    fun creationAsAtom() {
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
    fun cloneAsAtom() {

        atomsUnderTest.forEach {
            sequenceOf(Atom.of(it), Struct.of(it)).forEach {
                assertEquals(it, it.freshCopy())
                assertSame(it, it.freshCopy())
                assertTrue(it.structurallyEquals(it.freshCopy()))
                assertTrue(it.structurallyEquals(it.freshCopy()))
            }
        }
    }

    @Test
    fun typeTesting() {
        atomsUnderTest.forEach { value ->

            listOf(Atom.of(value), Struct.of(value)).forEach {
                assertTrue(it is Atom)
                assertTrue(it is Struct)
                assertTrue(it is Term)

                assertTrue(it.isAtom)
                assertTrue(it.isStruct)

                assertFalse(it is Numeric)
                assertFalse(it is Clause)
                assertFalse(it is Var)
                assertFalse(it is Couple)

                assertFalse(it.isNumber)
                assertFalse(it.isReal)
                assertFalse(it.isInt)
                assertFalse(it.isClause)
                assertFalse(it.isDirective)
                assertFalse(it.isFact)
                assertFalse(it.isRule)
                assertFalse(it.isCouple)
                assertFalse(it.isVariable)
                assertFalse(it.isVariable)
                assertFalse(it.isBound)
            }
        }
    }

    @Test
    fun valueAndFunctor() {
        atomsUnderTest.forEach { value ->

            listOf(Atom.of(value), Struct.of(value)).forEach {
                assertTrue(it is Atom)
                assertEquals(value, it.value)
                assertEquals(value, it.functor)
            }
        }
    }

    @Test
    fun zeroArity() {
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
    fun toStringRepresentation() {
        val values = atomsUnderTest
        val atomStrings = atomsUnderTest.map {
            if (it.matches(atomicPattern) || it in listOf("{}", "[]"))
                it
            else
                "'$it'"
        }

        sequenceOf<(String) -> Atom>(
                { Atom.of(it) },
                { Struct.of(it) as Atom }
        ).forEach {
            assertEquals(atomStrings, values.map(it).map(Atom::toString))
        }

    }

    @Test
    fun atomicFunctor() {
        atomsUnderTest.filter { it.matches(atomicPattern) }.forEach {
            listOf(Atom.of(it), Struct.of(it)).map { it as Atom }.forEach {
                assertTrue { it.isFunctorWellFormed }
            }
        }
    }
}