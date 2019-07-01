package it.unibo.tuprolog.core

import kotlin.test.Test
import kotlin.test.assertEquals

abstract class BaseTestAtom {

    abstract val atomsUnderTest: Array<String>

    @Test
    fun atomToStringRepresentation() { // TODO Put into future StructImpl test!
        val values = atomsUnderTest
        val atomStrings = atomsUnderTest.map {
            if (it.matches(Atom.ATOM_REGEX_PATTERN) || it in listOf("{}", "[]"))
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
}