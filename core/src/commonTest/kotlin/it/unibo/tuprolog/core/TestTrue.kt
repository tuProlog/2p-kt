package it.unibo.tuprolog.core

import kotlin.test.*

class TestTrue() : BaseTestAtom() {

    override val atomsUnderTest: Array<String>
        get() = arrayOf("true", "true ")

    val anAtom = atomsUnderTest[0]
    val anotherAtom = atomsUnderTest[1]

    @Test
    fun creation() {
        val emptyLists: Sequence<Term> = sequenceOf(
                Truth.`true`(),
                Truth.of(true),
                Atom.of(anAtom),
                atomOf(anAtom),
                Struct.of(anAtom),
                structOf(anAtom)
        )

        emptyLists.forEach {
            assertTrue(it is Truth)
            assertTrue(it is Atom)
            assertTrue(it is Struct)

            assertTrue(it.isTrue)
            assertTrue(it.isAtom)
            assertTrue(it.isStruct)

            assertFalse(it is EmptySet)
            assertFalse(it is Set)
            assertFalse(it is Couple)
            assertFalse(it is Clause)
            assertFalse(it is Number)
            assertFalse(it is Var)

            assertFalse(it.isEmptySet)
            assertFalse(it.isSet)
            assertFalse(it.isCouple)
            assertFalse(it.isClause)
            assertFalse(it.isNumber)
            assertFalse(it.isFail)
        }
    }

    @Test
    fun equality() {

        val atoms1 = sequenceOf(
                Truth.`true`(),
                Truth.of(true),
                Atom.of(anAtom),
                Struct.of(anAtom)
        )
        val atoms2 = sequenceOf(
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
        assertTrue(Atom.of(anAtom).isFunctorWellFormed)
    }

    @Test
    fun clone() {
        val emptyLists: Sequence<Term> = sequenceOf(
                Truth.`true`(),
                Truth.of(true),
                Atom.of(anAtom),
                Struct.of(anAtom)
        )

        emptyLists.forEach {
            assertEquals(it, it.freshCopy())
            assertSame(it, it.freshCopy())
            assertTrue(it.structurallyEquals(it.freshCopy()))
        }
    }
}