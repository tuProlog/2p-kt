package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.EmptySet
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.testutils.AtomUtils
import it.unibo.tuprolog.core.testutils.EqualityUtils
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test class for [AtomImpl] and [Atom]
 */
internal class AtomImplTest {

    // these could be randomly generated from some library in future, maybe starting from Atom regex
    private val correctAtoms = arrayOf(
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
    private val correctAtomInstances = correctAtoms.map { AtomImpl(it) }

    @Test
    fun functorCorrectness() {
        correctAtoms.zip(correctAtomInstances).forEach { (string, instance) ->
            assertEquals(string, instance.functor)
        }
    }

    @Test
    fun noArguments() {
        correctAtomInstances.forEach { AtomUtils.assertNoArguments(it) }
    }

    @Test
    fun zeroArity() {
        correctAtomInstances.forEach { AtomUtils.assertZeroArity(it) }
    }

    @Test
    fun testIsPropertiesAndTypes() {
        correctAtomInstances.forEach { TermTypeAssertionUtils.assertIsAtom(it) }
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
    fun emptySetAtomDetected() {
        assertTrue(AtomImpl("{}").isEmptySet)
    }

    @Test
    fun emptyListAtomDetected() {
        assertTrue(AtomImpl("[]").isEmptyList)
    }

    @Test
    fun trueAtomDetected() {
        assertTrue(AtomImpl("true").isTrue)
    }

    @Test
    fun failAtomDetected() {
        assertTrue(AtomImpl("fail").isFail)
    }

    @Test
    fun atomFunctorAndValueAreTheSame() {
        correctAtomInstances.forEach { AtomUtils.assertSameValueAndFunctor(it) }
    }

    @Test
    fun atomOfWorksAsExpected() {
        val toBeTested = correctAtoms.map { Atom.of(it) }

        EqualityUtils.assertElementsEqualities(toBeTested, correctAtomInstances)
    }

    @Test
    fun atomOfWorksWithNotableAtoms() {
        val notableAtoms = listOf("[]", "{}", "true", "fail")
        val notableAtomInstances = listOf(EmptyList(), EmptySet(), Truth.`true`(), Truth.fail())
        val toBeTested = notableAtoms.map { Atom.of(it) }

        EqualityUtils.assertElementsEqualities(toBeTested, notableAtomInstances)
    }

    @Test
    fun atomFreshCopyShouldReturnTheInstanceItself() {
        val testAtom = correctAtomInstances.first()

        AtomUtils.assertFreshCopyIsItself(testAtom)
    }
}
