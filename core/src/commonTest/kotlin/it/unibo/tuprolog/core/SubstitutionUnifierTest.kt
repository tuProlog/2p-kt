package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertNotStrictlyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.SubstitutionUtils
import kotlin.test.*

/**
 * Test class for [Substitution.Unifier] and [Substitution]
 *
 * @author Enrico
 */
internal class SubstitutionUnifierTest {

    private val aVar = Var.of("A")
    private val bVar = Var.of("B")
    private val xAtom = Atom.of("x")
    private val aVarToXAtomSubstitution = Substitution.Unifier(mapOf(aVar to xAtom))
    private val bVarToXAtomSubstitution = Substitution.Unifier(mapOf(bVar to xAtom))

    private val substitutions by lazy {
        SubstitutionUtils.mixedSubstitutions.map(Substitution::Unifier) +
                listOf(aVarToXAtomSubstitution, bVarToXAtomSubstitution)
    }

    @Test
    fun isSuccessIsTrue() {
        substitutions.forEach { assertTrue { it.isSuccess } }
    }

    @Test
    fun isFailedIsFalse() {
        substitutions.forEach { assertFalse { it.isFailed } }
    }

    @Test
    fun applyToShouldSubstituteVariableIfSameAsProvidedSubstitution() {
        val correct = SubstitutionUtils.termsWith(xAtom)
        val toBeTested = SubstitutionUtils.termsWith(aVar).map { aVarToXAtomSubstitution.applyTo(it) }

        onCorrespondingItems(correct, toBeTested, ::assertEqualities)
    }

    @Test
    fun applyToShouldNotSubstituteVariableIfNotSameAsProvidedSubstitution() {
        val correct = SubstitutionUtils.termsWith(Var.of("A"))
        val toBeTested = SubstitutionUtils.termsWith(Var.of("A")).map { aVarToXAtomSubstitution.applyTo(it) }

        onCorrespondingItems(correct, toBeTested) { expected, actual ->
            assertNotEquals(expected, actual)
            assertStructurallyEquals(expected, actual)
            assertNotStrictlyEquals(expected, actual)
        }
    }

    @Test
    fun applyToShouldNotSubstituteNotCorrespondingVariable() {
        val correct = SubstitutionUtils.termsWith(aVar)
        val toBeTested = SubstitutionUtils.termsWith(aVar).map { bVarToXAtomSubstitution.applyTo(it) }

        onCorrespondingItems(correct, toBeTested, ::assertEqualities)
    }

    @Test
    fun equalsWorksAsExpected() {
        assertEquals(Substitution.Unifier(mapOf(aVar to xAtom)), Substitution.Unifier(mapOf(aVar to xAtom)))
        assertNotEquals(Substitution.Unifier(mapOf(Var.of("A") to xAtom)), Substitution.Unifier(mapOf(Var.of("A") to xAtom)))
        assertNotEquals(aVarToXAtomSubstitution, bVarToXAtomSubstitution)
    }

}
