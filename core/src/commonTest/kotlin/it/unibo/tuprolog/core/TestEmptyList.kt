package it.unibo.tuprolog.core

import kotlin.test.*

class TestEmptyList() : BaseTestAtom() {
    override val atomsUnderTest: Array<String>
        get() = arrayOf("[]", "[ ]")

    val anAtom = atomsUnderTest[0]
    val anotherAtom = atomsUnderTest[1]

    @Test
    fun creation() {
        val emptyLists: Sequence<Term> = sequenceOf(
                EmptyList(),
                Empty.list(),
                List.empty(),
                lstOf(),
                List.of(),
                Atom.of(anAtom),
                atomOf(anAtom),
                Struct.of(anAtom),
                structOf(anAtom)
        )

        emptyLists.forEach {
            assertTrue(it is EmptyList)
            assertTrue(it is List)
            assertTrue(it is Empty)
            assertTrue(it is Atom)
            assertTrue(it is Struct)

            assertTrue(it.isEmptyList)
            assertTrue(it.isList)
            assertTrue(it.isAtom)
            assertTrue(it.isStruct)

            assertFalse(it is EmptySet)
            assertFalse(it is Set)
            assertFalse(it is Couple)
            assertFalse(it is Clause)
            assertFalse(it is Number)
            assertFalse(it is Truth)
            assertFalse(it is Var)

            assertFalse(it.isEmptySet)
            assertFalse(it.isSet)
            assertFalse(it.isCouple)
            assertFalse(it.isClause)
            assertFalse(it.isNumber)
            assertFalse(it.isTrue)
            assertFalse(it.isFail)
        }
    }

    @Test
    fun equality() {

        val atoms1 = sequenceOf(
                EmptyList(),
                Empty.list(),
                List.empty(),
                List.of(),
                Atom.of(anAtom),
                Struct.of(anAtom)
        )
        val atoms2 = sequenceOf(
                Atom.of(anotherAtom),
                Struct.of(anotherAtom),
                Atom.of(anotherAtom),
                Struct.of(anotherAtom),
                Atom.of(anotherAtom),
                Struct.of(anotherAtom)
        )



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
    fun nonAtomicFunctor() {
        assertFalse(Atom.of(anAtom).isFunctorWellFormed)
    }

    @Test
    fun clone() {
        val emptyLists: Sequence<Term> = sequenceOf(
                EmptyList(),
                Empty.list(),
                List.empty(),
                List.of(),
                Atom.of(anAtom),
                Struct.of(anAtom)
        )

        emptyLists.forEach {
            assertEquals(it, it.clone())
            assertSame(it, it.clone())
            assertTrue(it.structurallyEquals(it.clone()))
        }
    }
}