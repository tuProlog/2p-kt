package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Empty
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.testutils.EquationUtils
import it.unibo.tuprolog.unify.testutils.EquationUtils.assertAllIdentities
import it.unibo.tuprolog.unify.testutils.EquationUtils.assertAnyAssignment
import it.unibo.tuprolog.unify.testutils.EquationUtils.assertAnyContradiction
import it.unibo.tuprolog.unify.testutils.EquationUtils.assertNoComparisons
import it.unibo.tuprolog.unify.testutils.EquationUtils.assertNoIdentities
import it.unibo.tuprolog.unify.testutils.EquationUtils.countDeepGeneratedEquations
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import it.unibo.tuprolog.core.List as LogicList

/**
 * Test class for [Equation], its companion object, its subtypes, and the [UnificationUtils] extensions built on it
 */
internal class EquationTest {
    private val oneInstancePerType: List<Equation> by lazy {
        listOf(
            Equation.Identity(Atom.of("a"), Atom.of("a")),
            Equation.LeftAssignment(Var.of("X"), Atom.of("a")),
            Equation.RightAssignment(Atom.of("a"), Var.of("X")),
            Equation.Comparison(Struct.of("f", Var.of("X")), Struct.of("f", Var.of("Y"))),
            Equation.Contradiction(Atom.of("a"), Atom.of("b")),
        )
    }

    private fun assertOnlyDiscriminator(
        equation: Equation,
        isIdentity: Boolean = false,
        isAssignment: Boolean = false,
        isLeftAssignment: Boolean = false,
        isRightAssignment: Boolean = false,
        isComparison: Boolean = false,
        isContradiction: Boolean = false,
    ) {
        assertEquals(isIdentity, equation.isIdentity)
        assertEquals(isAssignment, equation.isAssignment)
        assertEquals(isLeftAssignment, equation.isLeftAssignment)
        assertEquals(isRightAssignment, equation.isRightAssignment)
        assertEquals(isComparison, equation.isComparison)
        assertEquals(isContradiction, equation.isContradiction)

        assertEquals(isIdentity, equation.asIdentity() === equation)
        assertEquals(isAssignment, equation.asAssignment() === equation)
        assertEquals(isLeftAssignment, equation.asLeftAssignment() === equation)
        assertEquals(isRightAssignment, equation.asRightAssignment() === equation)
        assertEquals(isComparison, equation.asComparison() === equation)
        assertEquals(isContradiction, equation.asContradiction() === equation)

        assertEquals(isIdentity, runCatching { equation.castToIdentity() }.getOrNull() === equation)
        assertEquals(isAssignment, runCatching { equation.castToAssignment() }.getOrNull() === equation)
        assertEquals(isLeftAssignment, runCatching { equation.castToLeftAssignment() }.getOrNull() === equation)
        assertEquals(isRightAssignment, runCatching { equation.castToRightAssignment() }.getOrNull() === equation)
        assertEquals(isComparison, runCatching { equation.castToComparison() }.getOrNull() === equation)
        assertEquals(isContradiction, runCatching { equation.castToContradiction() }.getOrNull() === equation)
    }

    // region construction

    @Test
    fun identityConstructorPreservesLhsAndRhs() {
        val (lhs, rhs) = Atom.of("a") to Atom.of("a")
        assertEquals(lhs to rhs, Equation.Identity(lhs, rhs).let { it.lhs to it.rhs })
    }

    @Test
    fun leftAssignmentConstructorPreservesLhsAndRhs() {
        val (lhs, rhs) = Var.of("X") to Atom.of("a")
        assertEquals(lhs to rhs, Equation.LeftAssignment(lhs, rhs).let { it.lhs to it.rhs })
    }

    @Test
    fun rightAssignmentConstructorPreservesLhsAndRhs() {
        val (lhs, rhs) = Atom.of("a") to Var.of("X")
        assertEquals(lhs to rhs, Equation.RightAssignment(lhs, rhs).let { it.lhs to it.rhs })
    }

    @Test
    fun comparisonConstructorPreservesLhsAndRhs() {
        val (lhs, rhs) = Struct.of("f", Var.of("X")) to Struct.of("f", Var.of("Y"))
        assertEquals(lhs to rhs, Equation.Comparison(lhs, rhs).let { it.lhs to it.rhs })
    }

    @Test
    fun contradictionConstructorPreservesLhsAndRhs() {
        val (lhs, rhs) = Atom.of("a") to Atom.of("b")
        assertEquals(lhs to rhs, Equation.Contradiction(lhs, rhs).let { it.lhs to it.rhs })
    }

    // endregion

    // region variable / term

    @Test
    fun leftAssignmentVariableAndTermMirrorLhsAndRhs() {
        val variable = Var.of("X")
        val term = Atom.of("a")
        val equation = Equation.LeftAssignment(variable, term)

        assertEquals(variable, equation.variable)
        assertEquals(term, equation.term)
    }

    @Test
    fun rightAssignmentVariableAndTermMirrorRhsAndLhs() {
        val variable = Var.of("X")
        val term = Atom.of("a")
        val equation = Equation.RightAssignment(term, variable)

        assertEquals(variable, equation.variable)
        assertEquals(term, equation.term)
    }

    // endregion

    // region type discriminators

    @Test
    fun identityDiscriminatorsAreConsistent() {
        assertOnlyDiscriminator(Equation.Identity(Atom.of("a"), Atom.of("a")), isIdentity = true)
    }

    @Test
    fun leftAssignmentDiscriminatorsAreConsistent() {
        assertOnlyDiscriminator(
            Equation.LeftAssignment(Var.of("X"), Atom.of("a")),
            isAssignment = true,
            isLeftAssignment = true,
        )
    }

    @Test
    fun rightAssignmentDiscriminatorsAreConsistent() {
        assertOnlyDiscriminator(
            Equation.RightAssignment(Atom.of("a"), Var.of("X")),
            isAssignment = true,
            isRightAssignment = true,
        )
    }

    @Test
    fun comparisonDiscriminatorsAreConsistent() {
        assertOnlyDiscriminator(
            Equation.Comparison(Struct.of("f", Var.of("X")), Struct.of("f", Var.of("Y"))),
            isComparison = true,
        )
    }

    @Test
    fun contradictionDiscriminatorsAreConsistent() {
        assertOnlyDiscriminator(Equation.Contradiction(Atom.of("a"), Atom.of("b")), isContradiction = true)
    }

    // endregion

    // region clone

    @Test
    fun cloneWithoutArgumentsReturnsAnEqualInstanceOfTheSameType() {
        oneInstancePerType.forEach { equation ->
            val cloned = equation.clone()
            assertEquals(equation, cloned)
            assertEquals(equation::class, cloned::class)
        }
    }

    @Test
    fun cloneWithNewArgumentsReplacesLhsAndRhs() {
        assertEquals(
            Equation.Identity(Atom.of("b"), Atom.of("c")),
            Equation.Identity(Atom.of("a"), Atom.of("a")).clone(Atom.of("b"), Atom.of("c")),
        )
        val newLeftVar = Var.of("Y")
        assertEquals(
            Equation.LeftAssignment(newLeftVar, Atom.of("b")),
            Equation.LeftAssignment(Var.of("X"), Atom.of("a")).clone(newLeftVar, Atom.of("b")),
        )
        val newRightVar = Var.of("Y")
        assertEquals(
            Equation.RightAssignment(Atom.of("b"), newRightVar),
            Equation.RightAssignment(Atom.of("a"), Var.of("X")).clone(Atom.of("b"), newRightVar),
        )
        assertEquals(
            Equation.Comparison(Atom.of("b"), Atom.of("c")),
            Equation.Comparison(Atom.of("a"), Atom.of("a")).clone(Atom.of("b"), Atom.of("c")),
        )
        assertEquals(
            Equation.Contradiction(Atom.of("b"), Atom.of("c")),
            Equation.Contradiction(Atom.of("a"), Atom.of("a")).clone(Atom.of("b"), Atom.of("c")),
        )
    }

    // endregion

    @Test
    fun toContradictionConvertsAnyEquationKeepingLhsAndRhs() {
        oneInstancePerType.forEach { equation ->
            assertEquals(Equation.Contradiction(equation.lhs, equation.rhs), equation.toContradiction())
        }
    }

    // region toAssignmentPair

    @Test
    fun toAssignmentPairOnLeftAssignmentReturnsVariableAndTerm() {
        val variable = Var.of("X")
        val term = Atom.of("a")
        assertEquals(variable to term, Equation.LeftAssignment(variable, term).toAssignmentPair())
    }

    @Test
    fun toAssignmentPairOnRightAssignmentReturnsVariableAndTerm() {
        val variable = Var.of("X")
        val term = Atom.of("a")
        assertEquals(variable to term, Equation.RightAssignment(term, variable).toAssignmentPair())
    }

    @Test
    fun toAssignmentPairOnNonAssignmentFallsBackToWhicheverSideIsAVariable() {
        val variable = Var.of("X")
        val term = Atom.of("a")
        assertEquals(variable to term, Equation.Identity(variable, term).toAssignmentPair())
        assertEquals(variable to term, Equation.Comparison(term, variable).toAssignmentPair())
    }

    @Test
    fun toAssignmentPairThrowsWhenNeitherSideIsAVariable() {
        assertFailsWith<IllegalArgumentException> {
            Equation.Contradiction(Atom.of("a"), Atom.of("b")).toAssignmentPair()
        }
        assertFailsWith<IllegalArgumentException> {
            Equation.Comparison(Atom.of("a"), Atom.of("a")).toAssignmentPair()
        }
    }

    // endregion

    // region toSubstitution

    @Test
    fun toSubstitutionOnLeftAssignmentBindsVariableToTerm() {
        val variable = Var.of("X")
        val term = Atom.of("a")
        assertEquals(Substitution.of(variable, term), Equation.LeftAssignment(variable, term).toSubstitution())
    }

    @Test
    fun toSubstitutionOnRightAssignmentBindsVariableToTerm() {
        val variable = Var.of("X")
        val term = Atom.of("a")
        assertEquals(Substitution.of(variable, term), Equation.RightAssignment(term, variable).toSubstitution())
    }

    @Test
    fun toSubstitutionOnContradictionIsAlwaysFailedRegardlessOfContent() {
        assertTrue(Equation.Contradiction(Atom.of("a"), Atom.of("b")).toSubstitution().isFailed)
        assertTrue(Equation.Contradiction(Var.of("X"), Var.of("X")).toSubstitution().isFailed)
    }

    @Test
    fun toSubstitutionOnNonAssignmentFallsBackToWhicheverSideIsAVariable() {
        val variable = Var.of("X")
        val term = Atom.of("a")
        assertEquals(Substitution.of(variable, term), Equation.Identity(variable, term).toSubstitution())
    }

    @Test
    fun toSubstitutionThrowsWhenNeitherSideIsAVariable() {
        assertFailsWith<IllegalArgumentException> {
            Equation.Comparison(Atom.of("a"), Atom.of("a")).toSubstitution()
        }
    }

    // endregion

    @Test
    fun toTermWrapsLhsAndRhsInEqualsStruct() {
        oneInstancePerType.forEach { equation ->
            assertEquals(Struct.of("=", equation.lhs, equation.rhs), equation.toTerm())
        }
    }

    // region toPair

    @Test
    fun toPairOnBaseEquationTypesReturnsLhsThenRhs() {
        val a = Atom.of("a")
        val b = Atom.of("b")
        assertEquals(a to b, Equation.Identity(a, b).toPair())
        assertEquals(a to b, Equation.Comparison(a, b).toPair())
        assertEquals(a to b, Equation.Contradiction(a, b).toPair())
    }

    @Test
    fun toPairOnLeftAssignmentReturnsVariableThenTerm() {
        val variable = Var.of("X")
        val term = Atom.of("a")
        assertEquals(variable to term, Equation.LeftAssignment(variable, term).toPair())
    }

    @Test
    fun toPairOnRightAssignmentReturnsTermThenVariable() {
        val variable = Var.of("X")
        val term = Atom.of("a")
        assertEquals(term to variable, Equation.RightAssignment(term, variable).toPair())
    }

    // endregion

    // region swap

    @Test
    fun swapInvertsIdentityComparisonAndContradiction() {
        // two differently-formatted but numerically-equal terms, so swap must reclassify both orderings as Identity
        val real1 = Real.of("1.5")
        val real2 = Real.of("1.500")
        assertEquals(Equation.Identity(real2, real1), Equation.Identity(real1, real2).swap())

        val a = Atom.of("a")
        val b = Atom.of("b")
        assertEquals(Equation.Contradiction(b, a), Equation.Contradiction(a, b).swap())

        val x = Var.of("X")
        val y = Var.of("Y")
        assertEquals(
            Equation.Comparison(Struct.of("f", y), Struct.of("f", x)),
            Equation.Comparison(Struct.of("f", x), Struct.of("f", y)).swap(),
        )
    }

    @Test
    fun swapTurnsLeftAssignmentIntoRightAssignmentPreservingVariableAndTerm() {
        val variable = Var.of("X")
        val term = Atom.of("a")
        val swapped = Equation.LeftAssignment(variable, term).swap()

        assertEquals(Equation.RightAssignment(term, variable), swapped)
        assertEquals(variable to term, swapped.toAssignmentPair())
    }

    @Test
    fun swapTurnsRightAssignmentIntoLeftAssignmentPreservingVariableAndTerm() {
        val variable = Var.of("X")
        val term = Atom.of("a")
        val swapped = Equation.RightAssignment(term, variable).swap()

        assertEquals(Equation.LeftAssignment(variable, term), swapped)
        assertEquals(variable to term, swapped.toAssignmentPair())
    }

    @Test
    fun swapOnVarToVarAssignmentAlwaysProducesLeftAssignment() {
        val x = Var.of("X")
        val y = Var.of("Y")
        val swapped = Equation.LeftAssignment(x, y).swap()

        assertEquals(Equation.LeftAssignment(y, x), swapped)
    }

    @Test
    fun doubleSwapReturnsTheOriginalEquationForEveryType() {
        oneInstancePerType.forEach { equation ->
            assertEquals(equation, equation.swap().swap())
        }
    }

    // endregion

    // region apply

    @Test
    fun applyOnLeftAssignmentReclassifiesToIdentityWhenSubstitutionMatches() {
        val x = Var.of("X")
        val a = Atom.of("a")

        assertEquals(Equation.Identity(a, a), Equation.LeftAssignment(x, a).apply(Substitution.of(x, a)))
    }

    @Test
    fun applyOnRightAssignmentReclassifiesToIdentityWhenSubstitutionMatches() {
        val x = Var.of("X")
        val a = Atom.of("a")

        assertEquals(Equation.Identity(a, a), Equation.RightAssignment(a, x).apply(Substitution.of(x, a)))
    }

    @Test
    fun applyLeavesUnrelatedAssignmentUnchanged() {
        val x = Var.of("X")
        val y = Var.of("Y")
        val a = Atom.of("a")

        assertEquals(Equation.LeftAssignment(x, a), Equation.LeftAssignment(x, a).apply(Substitution.of(y, a)))
    }

    @Test
    fun applyUsesProvidedEqualityCheckerToTestIdentity() {
        val x = Var.of("X")
        val a = Atom.of("a")

        val applied = Equation.LeftAssignment(x, a).apply(Substitution.of(x, a)) { _, _ -> false }

        assertFalse(applied.isIdentity)
        assertTrue(applied.isContradiction)
    }

    // endregion

    // region companion `of`

    @Test
    fun ofClassifiesEqualVariablesAsIdentity() {
        val x = Var.of("X")
        assertEquals(Equation.Identity(x, x), Equation.of(x, x))
    }

    @Test
    fun ofClassifiesDifferentVariablesAsLeftAssignmentNeverRightAssignment() {
        val x = Var.of("X")
        val y = Var.of("Y")
        assertEquals(Equation.LeftAssignment(x, y), Equation.of(x, y))
        assertEquals(Equation.LeftAssignment(y, x), Equation.of(y, x))
    }

    @Test
    fun ofClassifiesVariableAndTermWithVariableOnLhsAsLeftAssignment() {
        val x = Var.of("X")
        val a = Atom.of("a")
        assertEquals(Equation.LeftAssignment(x, a), Equation.of(x, a))
    }

    @Test
    fun ofClassifiesVariableAndTermWithVariableOnRhsAsRightAssignment() {
        val x = Var.of("X")
        val a = Atom.of("a")
        assertEquals(Equation.RightAssignment(a, x), Equation.of(a, x))
    }

    @Test
    fun ofClassifiesEqualConstantsAsIdentity() {
        assertEquals(Equation.Identity(Integer.of(1), Integer.of(1)), Equation.of(Integer.of(1), Integer.of(1)))
    }

    @Test
    fun ofClassifiesDifferentConstantsAsContradiction() {
        assertEquals(Equation.Contradiction(Integer.of(1), Integer.of(2)), Equation.of(Integer.of(1), Integer.of(2)))
    }

    @Test
    fun ofClassifiesConstantAndNonConstantNonVarTermAsContradiction() {
        val result = Equation.of(Integer.of(1), Struct.of("f", Atom.of("a")))
        assertTrue(result.isContradiction)
    }

    @Test
    fun ofClassifiesStructsWithMatchingFunctorAndArityAsComparison() {
        val lhs = Struct.of("f", Var.of("X"))
        val rhs = Struct.of("f", Var.of("Y"))
        assertEquals(Equation.Comparison(lhs, rhs), Equation.of(lhs, rhs))
    }

    @Test
    fun ofClassifiesStructsWithMismatchedArityAsContradiction() {
        val lhs = Struct.of("f", Var.of("X"))
        val rhs = Struct.of("f", Var.of("X"), Var.of("Y"))
        assertEquals(Equation.Contradiction(lhs, rhs), Equation.of(lhs, rhs))
    }

    @Test
    fun ofClassifiesStructsWithMismatchedFunctorAsContradiction() {
        val lhs = Struct.of("f", Var.of("X"))
        val rhs = Struct.of("g", Var.of("X"))
        assertEquals(Equation.Contradiction(lhs, rhs), Equation.of(lhs, rhs))
    }

    @Test
    fun ofPairDelegatesToOfWithLhsAndRhs() {
        val x = Var.of("X")
        val a = Atom.of("a")
        assertEquals(Equation.of(x, a), Equation.of(x to a))
        assertEquals(Equation.of(a, x), Equation.of(a to x))
    }

    @Test
    fun ofWithAlwaysFalseEqualityCheckerNeverProducesIdentity() {
        assertFalse(Equation.of(Atom.of("a"), Atom.of("a")) { _, _ -> false }.isIdentity)
        assertTrue(Equation.of(Var.of("X"), Var.of("X")) { _, _ -> false }.isLeftAssignment)
    }

    @Test
    fun ofWithAlwaysTrueEqualityCheckerOnlyAffectsVarVarAndConstantConstantBranches() {
        assertTrue(Equation.of(Integer.of(1), Integer.of(2)) { _, _ -> true }.isIdentity)
        assertTrue(Equation.of(Var.of("X"), Var.of("Y")) { _, _ -> true }.isIdentity)
        // struct arity/functor mismatch and struct/struct comparisons never consult the equality checker
        assertTrue(
            Equation.of(Struct.of("f", Atom.of("a")), Struct.of("g", Atom.of("a"))) { _, _ -> true }.isContradiction,
        )
        assertTrue(
            Equation.of(Struct.of("f", Var.of("X")), Struct.of("f", Var.of("Y"))) { _, _ -> true }.isComparison,
        )
    }

    // endregion

    // region companion `from`

    @Test
    fun fromSequenceFlatMapsAllOfOverEveryPair() {
        val pairs = sequenceOf(Var.of("X") to Atom.of("a"), Atom.of("a") to Atom.of("a"))
        assertEquals(pairs.flatMap { Equation.allOf(it) }.toList(), Equation.from(pairs).toList())
    }

    @Test
    fun fromIterableAndVarargDelegateToFromSequence() {
        val pairList = listOf(Var.of("X") to Atom.of("a"), Atom.of("a") to Atom.of("a"))
        val expected = Equation.from(pairList.asSequence()).toList()

        assertEquals(expected, Equation.from(pairList).toList())
        assertEquals(expected, Equation.from(*pairList.toTypedArray()).toList())
    }

    // endregion

    // region companion `allOf`

    @Test
    fun allOfOnAtomsProducesASingleEquation() {
        val a = Atom.of("a")
        assertEquals(listOf(Equation.Identity(a, a)), Equation.allOf(a, a).toList())
    }

    @Test
    fun allOfOnProperListsComparesElementsPairwiseAndTerminatorsIdentically() {
        val x = Var.of("X")
        val y = Var.of("Y")
        val lhs = LogicList.of(Atom.of("a"), x)
        val rhs = LogicList.of(Atom.of("a"), y)

        assertEquals(
            listOf(
                Equation.Identity(Atom.of("a"), Atom.of("a")),
                Equation.LeftAssignment(x, y),
                Equation.Identity(Empty.list(), Empty.list()),
            ),
            Equation.allOf(lhs, rhs).toList(),
        )
    }

    @Test
    fun allOfOnListsWithMismatchedLengthYieldsAContradiction() {
        val lhs = LogicList.of(Atom.of("a"))
        val rhs = LogicList.of(Atom.of("a"), Atom.of("b"))

        assertAnyContradiction(Equation.allOf(lhs, rhs))
    }

    @Test
    fun allOfOnImproperListsComparesHeadsAndDelegatesOnVarTails() {
        val v = Var.of("V")
        val w = Var.of("W")
        val lhs = Cons.of(Atom.of("a"), v)
        val rhs = Cons.of(Atom.of("a"), w)

        assertEquals(
            listOf(Equation.Identity(Atom.of("a"), Atom.of("a")), Equation.LeftAssignment(v, w)),
            Equation.allOf(lhs, rhs).toList(),
        )
    }

    @Test
    fun allOfOnTuplesComparesLeftElementsPairwise() {
        val x = Var.of("X")
        val y = Var.of("Y")
        val lhs = Tuple.of(Atom.of("a"), x)
        val rhs = Tuple.of(Atom.of("a"), y)

        assertEquals(
            listOf(Equation.Identity(Atom.of("a"), Atom.of("a")), Equation.LeftAssignment(x, y)),
            Equation.allOf(lhs, rhs).toList(),
        )
    }

    @Test
    fun allOfOnStructsWithMatchingFunctorAndArityRecursesIntoArguments() {
        val x = Var.of("X")
        val y = Var.of("Y")
        val lhs = Struct.of("f", Atom.of("a"), x)
        val rhs = Struct.of("f", Atom.of("a"), y)

        assertEquals(
            listOf(Equation.Identity(Atom.of("a"), Atom.of("a")), Equation.LeftAssignment(x, y)),
            Equation.allOf(lhs, rhs).toList(),
        )
    }

    @Test
    fun allOfOnStructsWithMismatchedArityOrFunctorYieldsASingleContradiction() {
        val mismatchedArity = Equation.allOf(Struct.of("f", Var.of("X")), Struct.of("f", Var.of("X"), Var.of("Y")))
        val mismatchedFunctor = Equation.allOf(Struct.of("f", Var.of("X")), Struct.of("g", Var.of("X")))

        assertEquals(1, mismatchedArity.count())
        assertTrue(mismatchedArity.single().isContradiction)
        assertEquals(1, mismatchedFunctor.count())
        assertTrue(mismatchedFunctor.single().isContradiction)
    }

    @Test
    fun allOfNeverProducesABareComparisonEvenWhenOfWould() {
        val lhs = Struct.of("f", Var.of("X"))
        val rhs = Struct.of("f", Var.of("Y"))

        assertTrue(Equation.of(lhs, rhs).isComparison)
        assertNoComparisons(Equation.allOf(lhs, rhs))
    }

    @Test
    fun allOfPairDelegatesToAllOfWithLhsAndRhs() {
        val lhs = Struct.of("f", Var.of("X"))
        val rhs = Struct.of("f", Var.of("Y"))
        assertEquals(Equation.allOf(lhs, rhs).toList(), Equation.allOf(lhs to rhs).toList())
    }

    @Test
    fun allOfUsesProvidedEqualityCheckerForLeafEquations() {
        assertNoIdentities(Equation.allOf(Struct.of("f", Atom.of("a")), Struct.of("f", Atom.of("a"))) { _, _ -> false })
    }

    // endregion

    // region bulk checks against EquationUtils fixtures

    @Test
    fun ofClassifiesAllShallowFixturesCorrectly() {
        val expected =
            EquationUtils.shallowIdentityEquations.map { (lhs, rhs) -> Equation.Identity(lhs, rhs) } +
                EquationUtils.assignmentEquationsShuffled.map { (lhs, rhs) ->
                    if (lhs.isVar) {
                        Equation.LeftAssignment(lhs.castToVar(), rhs)
                    } else {
                        Equation.RightAssignment(lhs, rhs.castToVar())
                    }
                } +
                EquationUtils.comparisonEquations.map { (lhs, rhs) -> Equation.Comparison(lhs, rhs) } +
                EquationUtils.shallowContradictionEquations.map { (lhs, rhs) -> Equation.Contradiction(lhs, rhs) }

        assertEquals(expected, EquationUtils.mixedShuffledShallowEquations.map { Equation.of(it) })
    }

    @Test
    fun ofPreservesAssignmentVariableTermMappingRegardlessOfOrientation() {
        val expected = EquationUtils.assignmentEquations.map { Equation.of(it).toAssignmentPair() }
        val actual = EquationUtils.assignmentEquationsShuffled.map { Equation.of(it).toAssignmentPair() }

        assertEquals(expected, actual)
    }

    @Test
    fun allOfClassifiesAllFixturesCorrectly() {
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
    fun allOfProducesOneEquationPerLeafForIdentityFixtures() {
        val expected = EquationUtils.allIdentityEquations.sumOf { (lhs, _) -> countDeepGeneratedEquations(lhs) }
        val actual = EquationUtils.allIdentityEquations.sumOf { Equation.allOf(it).count() }

        assertEquals(expected, actual)
    }

    @Test
    fun allOfPreservesAssignmentVariableTermMappingRegardlessOfOrientation() {
        val expected =
            EquationUtils.assignmentEquations.map { pair ->
                Equation.allOf(pair).map { it.toAssignmentPair() }.toList()
            }
        val actual =
            EquationUtils.assignmentEquationsShuffled.map { pair ->
                Equation.allOf(pair).map { it.toAssignmentPair() }.toList()
            }

        assertEquals(expected, actual)
    }

    @Test
    fun allOfNeverExposesBareComparisonAcrossAllFixtures() {
        EquationUtils.mixedShuffledAllEquations.map { Equation.allOf(it) }.forEach(::assertNoComparisons)
    }

    @Test
    fun swapPreservesAssignmentVariableTermMappingForNonVarToVarAssignments() {
        val nonVarToVarAssignments =
            EquationUtils.assignmentEquations.filterNot { (lhs, rhs) ->
                lhs.isVar && rhs.isVar
            }

        val expected = nonVarToVarAssignments.map { Equation.of(it).toAssignmentPair() }
        val actual = nonVarToVarAssignments.map { Equation.of(it).swap().toAssignmentPair() }

        assertEquals(expected, actual)
    }

    @Test
    fun swapCanInvertIdentityComparisonAndContradictionFixtures() {
        val testableItems =
            EquationUtils.allIdentityEquations + EquationUtils.allContradictionEquations +
                EquationUtils.comparisonEquations

        val expected = testableItems.map { (lhs, rhs) -> rhs to lhs }.map { Equation.of(it) }
        val actual = testableItems.map { Equation.of(it) }.map(Equation::swap)

        assertEquals(expected, actual)
    }

    @Test
    fun ofUsesProvidedEqualityCheckerAcrossAllFixtures() {
        assertNoIdentities(EquationUtils.mixedAllEquations.map { Equation.of(it) { _, _ -> false } }.asSequence())
    }

    @Test
    fun allOfUsesProvidedEqualityCheckerAcrossAllFixtures() {
        assertNoIdentities(
            EquationUtils.mixedAllEquations.flatMap { Equation.allOf(it) { _, _ -> false }.asIterable() }.asSequence(),
        )
    }

    @Test
    fun equationToSubstitutionMatchesSubstitutionBuiltFromRawPairs() {
        val expected = EquationUtils.assignmentEquations.map { Substitution.of(it) }
        val actual = EquationUtils.assignmentEquations.map { Equation.of(it).toSubstitution() }

        assertEquals(expected, actual)
    }

    // endregion

    // region UnificationUtils extensions

    @Test
    fun eqInfixDelegatesToEquationOf() {
        val x = Var.of("X")
        val a = Atom.of("a")
        assertEquals(Equation.of(x, a), x eq a)
        assertEquals(Equation.of(a, x), a eq x)
    }

    @Test
    fun iterableOfEquationsToSubstitutionMergesAllAssignments() {
        val equations = EquationUtils.assignmentEquations.map { Equation.of(it) }
        assertEquals(Substitution.of(EquationUtils.assignmentEquations), equations.toSubstitution())
    }

    @Test
    fun substitutionToEquationsAlwaysProducesLeftAssignments() {
        val substitution = Substitution.of(EquationUtils.assignmentEquations)
        val equations = substitution.toEquations()

        assertTrue(equations.all { it.isLeftAssignment })
        assertEquals(EquationUtils.assignmentEquations.map { Equation.of(it) }, equations)
    }

    // endregion
}
