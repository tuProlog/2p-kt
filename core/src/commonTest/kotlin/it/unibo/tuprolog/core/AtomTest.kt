package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.testutils.AtomUtils
import it.unibo.tuprolog.core.testutils.EqualityUtils
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Test class for [Atom] and its companion object
 *
 * @author Enrico
 */
internal class AtomTest {

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
    private val correctNonSpecialAtomInstances = correctNonSpecialAtoms.map { Atom.of(it) }

    private val correctSpecialAtoms = listOf(
            "[]",
            "{}",
            "true",
            "fail"
    )
    private val correctSpecialAtomInstances = correctSpecialAtoms.map { Atom.of(it) }


    @Test
    fun atomFunctorAndValueAreTheSame() {
        (correctNonSpecialAtomInstances + correctSpecialAtomInstances)
                .forEach(AtomUtils::assertSameValueAndFunctor)
    }

    @Test
    fun emptySetAtomDetected() {
        assertTrue(Atom.of("{}").isEmptySet)
    }

    @Test
    fun emptyListAtomDetected() {
        assertTrue(Atom.of("[]").isEmptyList)
    }

    @Test
    fun trueAtomDetected() {
        assertTrue(Atom.of("true").isTrue)
    }

    @Test
    fun failAtomDetected() {
        assertTrue(Atom.of("fail").isFail)
    }

    @Test
    fun atomOfWorksAsExpected() {
        val toBeTested = correctNonSpecialAtoms.map { Atom.of(it) }

        EqualityUtils.assertEqualities(toBeTested, correctNonSpecialAtomInstances)
    }

    @Test
    fun atomOfWorksWithNotableAtoms() {
        val notableAtomInstances = listOf(EmptyList(), EmptySet(), Truth.`true`(), Truth.fail())

        EqualityUtils.assertEqualities(correctSpecialAtomInstances, notableAtomInstances)
    }

    @Test
    fun atomIsAValidFunctor() {
        val correctAtoms = correctNonSpecialAtoms + correctSpecialAtoms
        val correctAtomInstances = correctNonSpecialAtomInstances + correctSpecialAtomInstances

        correctAtoms.zip(correctAtomInstances)
                .filter { (atomString, _) -> atomString.matches(Atom.ATOM_REGEX_PATTERN) }
                .forEach { (_, atomInstance) ->
                    assertTrue { atomInstance.isFunctorWellFormed }
                }
    }
}
