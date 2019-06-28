package it.unibo.tuprolog.core.integrationtests

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils.assertIsEmptySet
import kotlin.test.*
import it.unibo.tuprolog.core.Set.Companion as LogicSet

class TestEmptySet : BaseTestAtom() {

    override val atomsUnderTest: Array<String>
        get() = arrayOf("{}", "{ }")

    val anAtom = atomsUnderTest[0]
    val anotherAtom = atomsUnderTest[1]

    @Test
    fun variousCreationMethods() {
        val emptyLists: Sequence<Term> = sequenceOf(
                EmptySet(),
                Empty.set(),
                LogicSet.empty(),
                setOf(),
                LogicSet.of(),
                Atom.of(anAtom),
                atomOf(anAtom),
                Struct.of(anAtom),
                structOf(anAtom)
        )

        emptyLists.forEach { assertIsEmptySet(it) }
    }

    @Test
    fun equality() { // TODO test not clear, rewrite it

        val atoms1 = sequenceOf(
                EmptySet(),
                Empty.set(),
                LogicSet.empty(),
                LogicSet.of(),
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
                EmptySet(),
                Empty.set(),
                LogicSet.empty(),
                LogicSet.of(),
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
