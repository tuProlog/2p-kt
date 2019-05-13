package it.unibo.tuprolog.core

import kotlin.test.*

class TestAtom : BaseTestAtom() {

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