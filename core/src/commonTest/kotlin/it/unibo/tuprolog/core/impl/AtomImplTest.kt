package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.testutils.AtomUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test class for [AtomImpl] and [Atom]
 *
 * @author Enrico
 */
internal class AtomImplTest {

    // these could be randomly generated from some library in future, maybe starting from Atom regex
    private val correctNonSpecialAtoms = arrayOf(
            "anAtom",
            "AnUppercaseAtom",
            "_anAtomStartingWithUnderscore",
            "a_snake_cased_atom",
            "a string",
            "1",
            "1.3",
            "+",
            ",",
            "is",
            "!"
    )
    private val correctNonSpecialAtomInstances = correctNonSpecialAtoms.map(::AtomImpl)
    // for special atoms are intended atoms for which there's a known subclass, like: true, fail, [], {}

    @Test
    fun functorCorrectness() {
        correctNonSpecialAtoms.zip(correctNonSpecialAtomInstances).forEach { (string, instance) ->
            assertEquals(string, instance.functor)
        }
    }

    @Test
    fun noArguments() {
        correctNonSpecialAtomInstances.forEach(AtomUtils::assertNoArguments)
    }

    @Test
    fun zeroArity() {
        correctNonSpecialAtomInstances.forEach(AtomUtils::assertZeroArity)
    }

    @Test
    fun testIsPropertiesAndTypes() {
        correctNonSpecialAtomInstances.forEach(TermTypeAssertionUtils::assertIsAtom)
    }

    @Test
    fun strictlyEqualsWorksAsExpected() {
        val trueStruct = StructImpl("true", emptyArray())
        val trueAtom = AtomImpl("true")
        val trueTruth = Truth.`true`()

        // TODO review this behaviour, this is maybe incorrect
        assertTrue(trueStruct strictlyEquals trueAtom)
        assertFalse(trueAtom strictlyEquals trueStruct)

        assertTrue(trueAtom strictlyEquals trueTruth)
        assertTrue(trueTruth strictlyEquals trueAtom)
    }

    @Test
    fun structurallyEqualsWorksAsExpected() {
        val trueStruct = StructImpl("true", emptyArray())
        val trueAtom = AtomImpl("true")
        val trueTruth = Truth.`true`()

        assertTrue(trueStruct structurallyEquals trueAtom)
        assertTrue(trueAtom structurallyEquals trueStruct)

        assertTrue(trueAtom structurallyEquals trueTruth)
        assertTrue(trueTruth structurallyEquals trueAtom)
    }

    @Test
    fun atomFreshCopyShouldReturnTheInstanceItself() {
        correctNonSpecialAtomInstances.forEach(AtomUtils::assertFreshCopyIsItself)
    }
}
