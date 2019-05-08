package it.unibo.tuprolog.core

import kotlin.test.*

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

    @Test
    fun atomType() {
        val value = "anAtom"

        listOf(Atom.of(value), atomOf(value), Struct.of(value), value.toTerm()).forEach {
            assertTrue(it is Atom)
            assertTrue(it is Struct)
            assertTrue(it is Term)

            assertTrue(it !is Numeric)
            assertTrue(it !is Clause)
            assertTrue(it !is Var)
            assertTrue(it !is List)
            assertTrue(it !is Couple)
            assertTrue(it !is Set)
            assertTrue(it !is Empty)
            assertTrue(it !is Truth)
        }
    }

    @Test
    fun atomValueAndFunctor() {
        val value = "anAtom"

        listOf(Atom.of(value), atomOf(value), Struct.of(value), value.toTerm()).forEach {
            assertTrue(it is Atom)
            assertEquals(value, it.value)
            assertEquals(value, it.functor)
        }
    }

    @Test
    fun atomZeroArity() {
        val value = "anAtom"

        listOf(Atom.of(value), atomOf(value), Struct.of(value), value.toTerm()).forEach {
            assertTrue(it is Atom)
            assertEquals(0, it.arity)
            assertTrue(it.args.contentDeepEquals(emptyArray()))
            assertEquals(emptyList(), it.argsList)
            assertEquals(emptySequence(), it.argsSequence)
        }
    }

    @Test
    fun atomEquality() {
        val value1 = "anAtom"
        val value2 = "anotherAtom"

        val atoms1 = listOf(Atom.of(value1), atomOf(value1), Struct.of(value1), value1.toTerm())
        val atoms2 = listOf(Atom.of(value2), atomOf(value2), Struct.of(value2), value2.toTerm())


        for (a in atoms1) {
            for (b in atoms1) {
                assertEquals(a, b)
                assertTrue(a structurallyEquals b)
            }
            for (c in atoms2) {
                assertNotEquals(a, c)
                assertFalse(a structurallyEquals c)
            }
        }
    }

    @Test
    fun atomicFunctor() {
        assertTrue(Atom.of("anAtom").isFunctorWellFormed)
        assertFalse(Atom.of("1").isFunctorWellFormed)
        assertFalse(Atom.of("AnUppercaseAtom").isFunctorWellFormed)
        assertFalse(Atom.of("a string").isFunctorWellFormed)
    }

    @Test
    fun atomClone() {
        val values = listOf("anAtom", "AnUppercaseAtom", "a string", "1", "1.3")
        val atomStrings = listOf("anAtom", "'AnUppercaseAtom'", "'a string'", "'1'", "'1.3'")

        sequenceOf<(String)->Atom>(
                { Atom.of(it) },
                { atomOf(it) }
        ).forEach {
            assertEquals(atomStrings, values.map(it).map(Atom::toString))
        }

    }

    @Test
    fun atomToString() {
        val values = listOf("anAtom", "AnUppercaseAtom", "a string", "1", "1.3")
        val atomStrings = listOf("anAtom", "'AnUppercaseAtom'", "'a string'", "'1'", "'1.3'")

        sequenceOf<(String)->Atom>(
                { Atom.of(it) },
                { atomOf(it) }
        ).forEach {
            assertEquals(atomStrings, values.map(it).map(Atom::toString))
        }

    }

    @Test
    fun atomTests() {
        val value = "anAtom"

        listOf(Atom.of(value), atomOf(value), Struct.of(value), value.toTerm()).forEach {
            sequenceOf(
                it.isAtom,
                it.isStruct
            ).forEach { assertTrue(it) }

            sequenceOf(
                    it.isNumber,
                    it.isReal,
                    it.isInt,
                    it.isClause,
                    it.isDirective,
                    it.isFact,
                    it.isRule,
                    it.isTrue,
                    it.isFail,
                    it.isList,
                    it.isCouple,
                    it.isEmptyList,
                    it.isEmptySet
            ).forEach { assertFalse(it) }

            assertTrue(it !is Numeric)
            assertTrue(it !is Clause)
            assertTrue(it !is Var)
            assertTrue(it !is List)
            assertTrue(it !is Set)
            assertTrue(it !is Empty)
            assertTrue(it !is Truth)
        }
    }
}