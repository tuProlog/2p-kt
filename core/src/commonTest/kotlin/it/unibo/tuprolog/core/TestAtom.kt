package it.unibo.tuprolog.core

import kotlin.test.*

class TestAtom : BaseTestAtom() {

    val atomicPattern = """^[a-z][a-zA-Z0-9_]*$""".toRegex()

    override val atomsUnderTest: Array<String> = arrayOf("anAtom", "AnUppercaseAtom", "_anAtomStartingWithUnderscore",
            "a_snake_cased_atom", "a string", "1", "1.3", "+", ",", "is", "!")

    fun getOtherAtoms(thiz: String): Sequence<String> {
        return atomsUnderTest.asSequence().filter { it != thiz }
    }

    @Test
    fun atomEquality() {

        atomsUnderTest.forEach { value1 ->
            getOtherAtoms(value1).forEach { value2 ->

                val atoms1 = listOf(Atom.of(value1), Struct.of(value1))
                val atoms2 = listOf(Atom.of(value2), Struct.of(value2))


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

    @Test
    fun atomClone() {

        atomsUnderTest.forEach {
            sequenceOf(Atom.of(it), Struct.of(it)).forEach {
                assertEquals(it, it.clone())
                assertSame(it, it.clone())
                assertTrue(it.structurallyEquals(it.clone()))
                assertTrue(it.structurallyEquals(it.clone()))
            }
        }
    }

    @Test
    fun atomToString() {
        val values = atomsUnderTest
        val atomStrings = atomsUnderTest.map { if (it.matches(atomicPattern)) it else "'$it'" }

        sequenceOf<(String) -> Atom>(
                { Atom.of(it) },
                { Struct.of(it) as Atom }
        ).forEach {
            assertEquals(atomStrings, values.map(it).map(Atom::toString))
        }

    }

    @Test
    fun notWellKnown() {
        atomsUnderTest.forEach { value ->

            listOf(Atom.of(value), Struct.of(value)).forEach {

                assertFalse(it.isTrue)
                assertFalse(it.isFail)
                assertFalse(it.isList)
                assertFalse(it.isEmptyList)
                assertFalse(it.isEmptySet)

                assertFalse(it is List)
                assertFalse(it is Set)
                assertFalse(it is Empty)
                assertFalse(it is Truth)
            }
        }
    }

}