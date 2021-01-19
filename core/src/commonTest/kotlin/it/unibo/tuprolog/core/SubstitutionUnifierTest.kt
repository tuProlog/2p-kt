package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.SubstitutionUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Test class for [Substitution.unifier] and [Substitution]
 *
 * @author Enrico
 */
internal class SubstitutionUnifierTest {

    private val aVar = Var.of("A")
    private val bVar = Var.of("B")
    private val xAtom = Atom.of("x")
    private val aVarToXAtomSubstitution = Substitution.unifier(mapOf(aVar to xAtom))
    private val bVarToXAtomSubstitution = Substitution.unifier(mapOf(bVar to xAtom))

    private val substitutions by lazy {
        SubstitutionUtils.mixedSubstitutions.map(Substitution::unifier) +
            listOf(aVarToXAtomSubstitution, bVarToXAtomSubstitution)
    }

    @Test
    fun unifierConstructorReturnsEmptyUnifierIfIdentityMappingsDetected() {
        assertEquals(Substitution.unifier(emptyMap()), Substitution.unifier(mapOf(aVar to aVar)))
    }

    @Test
    fun unifierConstructorReturnsEmptyUnifierIfCircularIdentityMappingsDetected() {
        assertEquals(Substitution.unifier(emptyMap()), Substitution.unifier(mapOf(aVar to bVar, bVar to aVar)))
    }

    @Test
    fun unifierConstructorReturnsTrimmedVariableChain() {
        val correct = Substitution.unifier(mapOf(aVar to xAtom, bVar to xAtom))
        val toBeTested = Substitution.unifier(mapOf(aVar to bVar, bVar to xAtom))
        assertEquals(correct, toBeTested)
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
        assertEquals(Substitution.unifier(mapOf(aVar to xAtom)), Substitution.unifier(mapOf(aVar to xAtom)))
        assertNotEquals(
            Substitution.unifier(mapOf(Var.of("A") to xAtom)),
            Substitution.unifier(mapOf(Var.of("A") to xAtom))
        )
        assertNotEquals(aVarToXAtomSubstitution, bVarToXAtomSubstitution)
    }

    @Test
    fun plusOtherSubstitutionPutsSubstitutionsTogether() {
        val correct =
            Substitution.of((aVarToXAtomSubstitution.entries + bVarToXAtomSubstitution.entries).map { it.toPair() })
        val toBeTested = aVarToXAtomSubstitution + bVarToXAtomSubstitution

        assertEquals(correct, toBeTested)
    }

    @Test
    fun plusOtherSubstitutionReplacesPresentVariables() {
        val myAtom = Atom.of("hello")
        val xVar = Var.of("X")
        val yVar = Var.of("Y")

        val correct = Substitution.of(xVar to myAtom, yVar to myAtom)
        val toBeTested = Substitution.of(xVar to yVar) + Substitution.of(yVar to myAtom)

        assertEquals(correct, toBeTested)
    }

    @Test
    fun plusOtherSubstitutionIsIdempotentIfSameSubstitution() {
        assertEquals(aVarToXAtomSubstitution, aVarToXAtomSubstitution + aVarToXAtomSubstitution)
    }

    @Test
    fun plusOtherSubstitutionContradictingBaseOneReturnsFailedSubstitution() {
        val toBeTested = listOf(
            Substitution.of(bVar to Truth.TRUE) + bVarToXAtomSubstitution,
            bVarToXAtomSubstitution + Substitution.of(bVar to Truth.TRUE)
        )

        toBeTested.forEach { assertEquals(Substitution.failed(), it) }
    }

    @Test
    fun plusFailedSubstitutionReturnFailedSubstitution() {
        substitutions.forEach {
            assertEquals(Substitution.failed(), it + Substitution.failed())
            assertEquals(Substitution.failed(), Substitution.failed() + it)
        }
    }

    @Test
    fun plusComplianceWithCompositionDefinedInStandardProlog() {
        val first = Substitution.unifier(mapOf(bVar to Struct.of("f", aVar)))
        val second = Substitution.unifier(mapOf(aVar to xAtom))

        val firstComposedSecondExpected = Substitution.unifier(
            mapOf(
                bVar to Struct.of("f", xAtom),
                aVar to xAtom
            )
        )

        val secondComposedFirstExpected = Substitution.unifier(
            mapOf(
                aVar to xAtom,
                bVar to Struct.of("f", aVar)
            )
        )

        assertEquals(firstComposedSecondExpected, first + second)
        assertEquals(secondComposedFirstExpected, second + first)
    }

    @Test
    fun minusVariablesIterableRemovesCorrectBindings() {
        val correct = aVarToXAtomSubstitution
        val toBeTested = (aVarToXAtomSubstitution + bVarToXAtomSubstitution) - listOf(bVar)

        assertEquals(correct, toBeTested)
    }

    @Test
    fun minusVariableIterableWithNoCommonVariableDoesNothing() {
        assertEquals(aVarToXAtomSubstitution, aVarToXAtomSubstitution - listOf(bVar))
    }

    @Test
    fun minusOtherSubstitutionRemovesCorrectBindings() {
        val correct = aVarToXAtomSubstitution
        val toBeTested = (aVarToXAtomSubstitution + bVarToXAtomSubstitution) - bVarToXAtomSubstitution

        assertEquals(correct, toBeTested)
    }

    @Test
    fun minusOtherSubstitutionWithNoCommonVariablesDoesNothing() {
        assertEquals(aVarToXAtomSubstitution, aVarToXAtomSubstitution - bVarToXAtomSubstitution)
    }

    @Test
    fun minusFailedSubstitutionDoesNothing() {
        assertEquals(aVarToXAtomSubstitution, aVarToXAtomSubstitution - Substitution.failed())
    }

    @Test
    fun filterMapEntryReturnsOnlyCorrectBindings() {
        assertEquals(
            aVarToXAtomSubstitution,
            (aVarToXAtomSubstitution + bVarToXAtomSubstitution).filter { (`var`, _) -> `var` == aVar }
        )
        assertEquals(Substitution.empty(), bVarToXAtomSubstitution.filter { (`var`, _) -> `var` == aVar })
    }

    @Test
    fun filterPredicateReturnsOnlyCorrectBindings() {
        assertEquals(
            aVarToXAtomSubstitution,
            (aVarToXAtomSubstitution + bVarToXAtomSubstitution).filter { `var`, _ -> `var` == aVar }
        )
        assertEquals(Substitution.empty(), bVarToXAtomSubstitution.filter { `var`, _ -> `var` == aVar })
    }

    @Test
    fun getReturnsLastBoundTermInTheVariableChain() {
        val myAtom = Atom.of("hello")
        val xVar = Var.of("X")
        val yVar = Var.of("Y")

        val toBeTested = Substitution.of(xVar to yVar) + Substitution.of(yVar to myAtom)

        assertEquals(myAtom, toBeTested[xVar])
    }
}
