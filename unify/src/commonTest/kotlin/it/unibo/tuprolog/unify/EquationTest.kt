package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.testutils.EquationUtils
import it.unibo.tuprolog.unify.testutils.EquationUtils.assertAllIdentities
import it.unibo.tuprolog.unify.testutils.EquationUtils.assertAnyAssignment
import it.unibo.tuprolog.unify.testutils.EquationUtils.assertAnyContradiction
import it.unibo.tuprolog.unify.testutils.EquationUtils.assertNoComparisons
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Test class for [Equation], its companion object and internal classes
 *
 * @author Enrico
 */
internal class EquationTest {

    /** Correct instances of equations, whose type is recognizable without exploring in deep the components */
    private val correctShallowEquationsInstances =
            EquationUtils.shallowIdentityEquations.map { (lhs, rhs) -> Equation.Identity(lhs, rhs) } +
                    EquationUtils.assignmentEquations.map { (lhs, rhs) -> Equation.Assignment(lhs, rhs) } +
                    EquationUtils.comparisonEquations.map { (lhs, rhs) -> Equation.Comparison(lhs, rhs) } +
                    EquationUtils.shallowContradictionEquations.map { (lhs, rhs) -> Equation.Contradiction(lhs, rhs) }

    @Test
    fun identityLhsAndRhsCorrect() {
        val toBeTested = EquationUtils.allIdentityEquations.map { (lhs, rhs) -> Equation.Identity(lhs, rhs) }

        assertEquals(EquationUtils.allIdentityEquations, toBeTested.map { (lhs, rhs) -> Pair(lhs, rhs) })
    }

    @Test
    fun assignmentLhsAndRhsCorrect() {
        val toBeTested = EquationUtils.assignmentEquations.map { (lhs, rhs) -> Equation.Assignment(lhs, rhs) }

        assertEquals(EquationUtils.assignmentEquations, toBeTested.map { (lhs, rhs) -> Pair(lhs, rhs) })
    }

    @Test
    fun comparisionLhsAndRhsCorrect() {
        val toBeTested = EquationUtils.comparisonEquations.map { (lhs, rhs) -> Equation.Comparison(lhs, rhs) }

        assertEquals(EquationUtils.comparisonEquations, toBeTested.map { (lhs, rhs) -> Pair(lhs, rhs) })
    }

    @Test
    fun contradictionLhsAndRhsCorrect() {
        val toBeTested = EquationUtils.allContradictionEquations.map { (lhs, rhs) -> Equation.Contradiction(lhs, rhs) }

        assertEquals(EquationUtils.allContradictionEquations, toBeTested.map { (lhs, rhs) -> Pair(lhs, rhs) })
    }

    @Test
    fun equationOfLhsAndRhsGivesCorrectInstance() {
        val toBeTested = EquationUtils.mixedShuffledShallowEquations.map { (lhs, rhs) -> Equation.of(lhs, rhs) }

        assertEquals(correctShallowEquationsInstances, toBeTested)
    }

    @Test
    fun equationOfPairGivesCorrectInstance() {
        val toBeTested = EquationUtils.mixedShuffledShallowEquations.map { Equation.of(it) }

        assertEquals(correctShallowEquationsInstances, toBeTested)
    }

    @Test
    fun equationOfAutomaticallySwapsAssignments() {
        val correct = EquationUtils.assignmentEquations.map { (lhs, rhs) -> Equation.of(lhs, rhs) } +
                EquationUtils.assignmentEquations.map { Equation.of(it) }

        val toBeTested = EquationUtils.assignmentEquationsShuffled.map { (lhs, rhs) -> Equation.of(lhs, rhs) } +
                EquationUtils.assignmentEquationsShuffled.map { Equation.of(it) }

        assertEquals(correct, toBeTested)
    }

    @Test
    fun equationAllOfLhsAndRhsGivesCorrectInstances() {
        EquationUtils.allIdentityEquations.map { (lhs, rhs) -> Equation.allOf(lhs, rhs) }.forEach(::assertAllIdentities)

        EquationUtils.assignmentEquationsShuffled.map { (lhs, rhs) -> Equation.allOf(lhs, rhs) }.forEach { eqSequence ->
            assertAnyAssignment(eqSequence)
            assertNoComparisons(eqSequence)
        }

        EquationUtils.comparisonEquations.map { (lhs, rhs) -> Equation.allOf(lhs, rhs) }.forEach(::assertNoComparisons)

        EquationUtils.allContradictionEquations.map { (lhs, rhs) -> Equation.allOf(lhs, rhs) }.forEach { eqSequence ->
            assertAnyContradiction(eqSequence)
            assertNoComparisons(eqSequence)
        }
    }

    @Test
    fun equationAllOfPairGivesCorrectInstances() {
        EquationUtils.allIdentityEquations.map { Equation.allOf(it) }.forEach(::assertAllIdentities)

        EquationUtils.assignmentEquationsShuffled.map { Equation.allOf(it) }.forEach { eqSequence ->
            assertAnyAssignment(eqSequence)
            assertNoComparisons(eqSequence)
        }

        EquationUtils.comparisonEquations.map { Equation.allOf(it) }.forEach(::assertNoComparisons)

        EquationUtils.allContradictionEquations.map { Equation.allOf(it) }.forEach { eqSequence ->
            assertAnyContradiction(eqSequence)
            assertNoComparisons(eqSequence)
        }
    }

    @Test
    fun equationAllOfAutomaticallySwapsAssignments() {
        val correct = EquationUtils.assignmentEquations.map { (lhs, rhs) -> Equation.allOf(lhs, rhs).toList() } +
                EquationUtils.assignmentEquations.map { Equation.allOf(it).toList() }

        val toBeTested = EquationUtils.assignmentEquationsShuffled.map { (lhs, rhs) -> Equation.allOf(lhs, rhs).toList() } +
                EquationUtils.assignmentEquationsShuffled.map { Equation.allOf(it).toList() }

        assertEquals(correct, toBeTested)
    }

    @Test
    fun equationAllOfShouldNeverReturnComparisonAsItAlwaysInspectsTermsInDeep() {
        EquationUtils.mixedShuffledAllEquations.map { (lhs, rhs) -> Equation.allOf(lhs, rhs) }.forEach(::assertNoComparisons)
        EquationUtils.mixedShuffledAllEquations.map { Equation.allOf(it) }.forEach(::assertNoComparisons)
    }

    @Test
    fun toPairWorksAsExpected() {
        val toBeTested = EquationUtils.mixedAllEquations.map { Equation.of(it) }.map(Equation<*, *>::toPair)

        assertEquals(EquationUtils.mixedAllEquations, toBeTested)
    }

    @Test
    fun swapCanInvertAllInvertibleEquations() {
        val testableItems = EquationUtils.allIdentityEquations +
                EquationUtils.allContradictionEquations +
                EquationUtils.comparisonEquations

        val correct = testableItems.map { (lhs, rhs) -> rhs to lhs }.map { Equation.of(it) }
        val toBeTested = testableItems.map { Equation.of(it) }.map(Equation<*, *>::swap)

        assertEquals(correct, toBeTested)
    }

    @Test
    fun swapCannotInvertAssignments() {
        val correct = EquationUtils.assignmentEquations.map { Equation.of(it) }
        val toBeTested = EquationUtils.assignmentEquations.map { Equation.of(it) }.map(Equation<*, *>::swap)

        assertEquals(correct, toBeTested)
    }

    @Test
    fun equationOfLhsAndRhsUsesProvidedEqualityCheckerToTestIdentity() {
        EquationUtils.mixedAllEquations.map { (lhs, rhs) -> Equation.of(lhs, rhs, { _, _ -> false }) }.forEach {
            assertFalse { it is Equation.Identity<*> }
        }
    }

    @Test
    fun equationOfPairUsesProvidedEqualityCheckerToTestIdentity() {
        EquationUtils.mixedAllEquations.map { Equation.of(it) { _, _ -> false } }.forEach {
            assertFalse { it is Equation.Identity<*> }
        }
    }

    @Test
    fun equationAllOfLhsAndRhsUsesProvidedEqualityCheckerToTestIdentity() {
        EquationUtils.mixedAllEquations.map { (lhs, rhs) -> Equation.allOf(lhs, rhs, { _, _ -> false }).asIterable() }
                .flatten()
                .forEach {
                    assertFalse { it is Equation.Identity<*> }
                }
    }

    @Test
    fun equationAllOfPairUsesProvidedEqualityCheckerToTestIdentity() {
        EquationUtils.mixedAllEquations.map { Equation.allOf(it) { _, _ -> false }.asIterable() }
                .flatten()
                .forEach {
                    assertFalse { it is Equation.Identity<*> }
                }
    }

    @Test
    fun applyWorksAsExpected() {
        val aAtom = Atom.of("a")

        val correct = aAtom `=` aAtom
        val toBeTested = (aAtom `=` Var.of("A")).apply(Substitution.of("A", aAtom))

        assertEquals(correct, toBeTested)
        assertTrue(toBeTested is Equation.Identity<*>)
    }

    @Test
    fun applyUsesProvidedEqualityCheckerToTestIdentity() {
        val aAtom = Atom.of("a")
        val toBeTested = (aAtom `=` Var.of("A")).apply(Substitution.of("A", aAtom)) { _, _ -> false }

        assertFalse(toBeTested is Equation.Identity<*>)
    }

    @Test
    fun equationToSubstitutionWorksAsExpected() {
        val correct = EquationUtils.assignmentEquations.map { Substitution.of(it) }

        @Suppress("UNCHECKED_CAST")
        val toBeTested = EquationUtils.assignmentEquations
                .map { Equation.of(it) as Equation<Var, Term> }
                .map { it.toSubstitution() }

        assertEquals(correct, toBeTested)
    }

    @Test
    fun iterableOfEquationsToSubstitutionWorksAsExpected() {
        val correct = Substitution.of(EquationUtils.assignmentEquations)

        @Suppress("UNCHECKED_CAST")
        val toBeTested = EquationUtils.assignmentEquations
                .map { Equation.of(it) as Equation<Var, Term> }
                .toSubstitution()

        assertEquals(correct, toBeTested)
    }

    @Test
    fun toEquationsWorksAsExpected() {
        val correct = EquationUtils.assignmentEquations.map { Equation.of(it) }
        val toBeTested = EquationUtils.assignmentEquations.map { Substitution.of(it) }.flatMap { it.toEquations() }

        assertEquals(correct, toBeTested)
    }

    @Test
    fun symbolicEqualsCreatesCorrectEquationInstances() {
        val correct = EquationUtils.mixedAllEquations.map { Equation.of(it) }
        val toBeTested = EquationUtils.mixedAllEquations.map { (lhs, rhs) -> lhs `=` rhs }

        assertEquals(correct, toBeTested)
    }
}
