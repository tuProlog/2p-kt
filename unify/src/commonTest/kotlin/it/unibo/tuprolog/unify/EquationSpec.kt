package it.unibo.tuprolog.unify

import io.kotest.core.spec.style.FunSpec
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
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import it.unibo.tuprolog.core.List as LogicList

/**
 * Test class for [Equation], its companion object, its subtypes, and the [UnificationUtils] extensions built on it
 */
class EquationSpec :
    FunSpec({
        val oneInstancePerType: List<Equation> by lazy {
            listOf(
                Equation.Identity(Atom.of("a"), Atom.of("a")),
                Equation.LeftAssignment(Var.of("X"), Atom.of("a")),
                Equation.RightAssignment(Atom.of("a"), Var.of("X")),
                Equation.Comparison(Struct.of("f", Var.of("X")), Struct.of("f", Var.of("Y"))),
                Equation.Contradiction(Atom.of("a"), Atom.of("b")),
            )
        }

        fun assertOnlyDiscriminator(
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
        test("identity-constructor-preserves-lhs-and-rhs") {
            val (lhs, rhs) = Atom.of("a") to Atom.of("a")
            assertEquals(lhs to rhs, Equation.Identity(lhs, rhs).let { it.lhs to it.rhs })
        }

        test("left-assignment-constructor-preserves-lhs-and-rhs") {
            val (lhs, rhs) = Var.of("X") to Atom.of("a")
            assertEquals(lhs to rhs, Equation.LeftAssignment(lhs, rhs).let { it.lhs to it.rhs })
        }

        test("right-assignment-constructor-preserves-lhs-and-rhs") {
            val (lhs, rhs) = Atom.of("a") to Var.of("X")
            assertEquals(lhs to rhs, Equation.RightAssignment(lhs, rhs).let { it.lhs to it.rhs })
        }

        test("comparison-constructor-preserves-lhs-and-rhs") {
            val (lhs, rhs) = Struct.of("f", Var.of("X")) to Struct.of("f", Var.of("Y"))
            assertEquals(lhs to rhs, Equation.Comparison(lhs, rhs).let { it.lhs to it.rhs })
        }

        test("contradiction-constructor-preserves-lhs-and-rhs") {
            val (lhs, rhs) = Atom.of("a") to Atom.of("b")
            assertEquals(lhs to rhs, Equation.Contradiction(lhs, rhs).let { it.lhs to it.rhs })
        }

        // endregion
        // region variable / term
        test("left-assignment-variable-and-term-mirror-lhs-and-rhs") {
            val variable = Var.of("X")
            val term = Atom.of("a")
            val equation = Equation.LeftAssignment(variable, term)

            assertEquals(variable, equation.variable)
            assertEquals(term, equation.term)
        }

        test("right-assignment-variable-and-term-mirror-rhs-and-lhs") {
            val variable = Var.of("X")
            val term = Atom.of("a")
            val equation = Equation.RightAssignment(term, variable)

            assertEquals(variable, equation.variable)
            assertEquals(term, equation.term)
        }

        // endregion
        // region type discriminators
        test("identity-discriminators-are-consistent") {
            assertOnlyDiscriminator(Equation.Identity(Atom.of("a"), Atom.of("a")), isIdentity = true)
        }

        test("left-assignment-discriminators-are-consistent") {
            assertOnlyDiscriminator(
                Equation.LeftAssignment(Var.of("X"), Atom.of("a")),
                isAssignment = true,
                isLeftAssignment = true,
            )
        }

        test("right-assignment-discriminators-are-consistent") {
            assertOnlyDiscriminator(
                Equation.RightAssignment(Atom.of("a"), Var.of("X")),
                isAssignment = true,
                isRightAssignment = true,
            )
        }

        test("comparison-discriminators-are-consistent") {
            assertOnlyDiscriminator(
                Equation.Comparison(Struct.of("f", Var.of("X")), Struct.of("f", Var.of("Y"))),
                isComparison = true,
            )
        }

        test("contradiction-discriminators-are-consistent") {
            assertOnlyDiscriminator(Equation.Contradiction(Atom.of("a"), Atom.of("b")), isContradiction = true)
        }

        // endregion
        // region clone
        test("clone-without-arguments-returns-an-equal-instance-of-the-same-type") {
            oneInstancePerType.forEach { equation ->
                val cloned = equation.clone()
                assertEquals(equation, cloned)
                assertEquals(equation::class, cloned::class)
            }
        }

        test("clone-with-new-arguments-replaces-lhs-and-rhs") {
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
        test("to-contradiction-converts-any-equation-keeping-lhs-and-rhs") {
            oneInstancePerType.forEach { equation ->
                assertEquals(Equation.Contradiction(equation.lhs, equation.rhs), equation.toContradiction())
            }
        }

        // region toAssignmentPair
        test("to-assignment-pair-on-left-assignment-returns-variable-and-term") {
            val variable = Var.of("X")
            val term = Atom.of("a")
            assertEquals(variable to term, Equation.LeftAssignment(variable, term).toAssignmentPair())
        }

        test("to-assignment-pair-on-right-assignment-returns-variable-and-term") {
            val variable = Var.of("X")
            val term = Atom.of("a")
            assertEquals(variable to term, Equation.RightAssignment(term, variable).toAssignmentPair())
        }

        test("to-assignment-pair-on-non-assignment-falls-back-to-whichever-side-is-a-variable") {
            val variable = Var.of("X")
            val term = Atom.of("a")
            assertEquals(variable to term, Equation.Identity(variable, term).toAssignmentPair())
            assertEquals(variable to term, Equation.Comparison(term, variable).toAssignmentPair())
        }

        test("to-assignment-pair-throws-when-neither-side-is-a-variable") {
            assertFailsWith<IllegalArgumentException> {
                Equation.Contradiction(Atom.of("a"), Atom.of("b")).toAssignmentPair()
            }
            assertFailsWith<IllegalArgumentException> {
                Equation.Comparison(Atom.of("a"), Atom.of("a")).toAssignmentPair()
            }
        }

        // endregion
        // region toSubstitution
        test("to-substitution-on-left-assignment-binds-variable-to-term") {
            val variable = Var.of("X")
            val term = Atom.of("a")
            assertEquals(Substitution.of(variable, term), Equation.LeftAssignment(variable, term).toSubstitution())
        }

        test("to-substitution-on-right-assignment-binds-variable-to-term") {
            val variable = Var.of("X")
            val term = Atom.of("a")
            assertEquals(Substitution.of(variable, term), Equation.RightAssignment(term, variable).toSubstitution())
        }

        test("to-substitution-on-contradiction-is-always-failed-regardless-of-content") {
            assertTrue(Equation.Contradiction(Atom.of("a"), Atom.of("b")).toSubstitution().isFailed)
            assertTrue(Equation.Contradiction(Var.of("X"), Var.of("X")).toSubstitution().isFailed)
        }

        test("to-substitution-on-non-assignment-falls-back-to-whichever-side-is-a-variable") {
            val variable = Var.of("X")
            val term = Atom.of("a")
            assertEquals(Substitution.of(variable, term), Equation.Identity(variable, term).toSubstitution())
        }

        test("to-substitution-throws-when-neither-side-is-a-variable") {
            assertFailsWith<IllegalArgumentException> {
                Equation.Comparison(Atom.of("a"), Atom.of("a")).toSubstitution()
            }
        }

        // endregion
        test("to-term-wraps-lhs-and-rhs-in-equals-struct") {
            oneInstancePerType.forEach { equation ->
                assertEquals(Struct.of("=", equation.lhs, equation.rhs), equation.toTerm())
            }
        }

        // region toPair
        test("to-pair-on-base-equation-types-returns-lhs-then-rhs") {
            val a = Atom.of("a")
            val b = Atom.of("b")
            assertEquals(a to b, Equation.Identity(a, b).toPair())
            assertEquals(a to b, Equation.Comparison(a, b).toPair())
            assertEquals(a to b, Equation.Contradiction(a, b).toPair())
        }

        test("to-pair-on-left-assignment-returns-variable-then-term") {
            val variable = Var.of("X")
            val term = Atom.of("a")
            assertEquals(variable to term, Equation.LeftAssignment(variable, term).toPair())
        }

        test("to-pair-on-right-assignment-returns-term-then-variable") {
            val variable = Var.of("X")
            val term = Atom.of("a")
            assertEquals(term to variable, Equation.RightAssignment(term, variable).toPair())
        }

        // endregion
        // region swap
        test("swap-inverts-identity-comparison-and-contradiction") {
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

        test("swap-turns-left-assignment-into-right-assignment-preserving-variable-and-term") {
            val variable = Var.of("X")
            val term = Atom.of("a")
            val swapped = Equation.LeftAssignment(variable, term).swap()

            assertEquals(Equation.RightAssignment(term, variable), swapped)
            assertEquals(variable to term, swapped.toAssignmentPair())
        }

        test("swap-turns-right-assignment-into-left-assignment-preserving-variable-and-term") {
            val variable = Var.of("X")
            val term = Atom.of("a")
            val swapped = Equation.RightAssignment(term, variable).swap()

            assertEquals(Equation.LeftAssignment(variable, term), swapped)
            assertEquals(variable to term, swapped.toAssignmentPair())
        }

        test("swap-on-var-to-var-assignment-always-produces-left-assignment") {
            val x = Var.of("X")
            val y = Var.of("Y")
            val swapped = Equation.LeftAssignment(x, y).swap()

            assertEquals(Equation.LeftAssignment(y, x), swapped)
        }

        test("double-swap-returns-the-original-equation-for-every-type") {
            oneInstancePerType.forEach { equation ->
                assertEquals(equation, equation.swap().swap())
            }
        }

        // endregion
        // region apply
        test("apply-on-left-assignment-reclassifies-to-identity-when-substitution-matches") {
            val x = Var.of("X")
            val a = Atom.of("a")

            assertEquals(Equation.Identity(a, a), Equation.LeftAssignment(x, a).apply(Substitution.of(x, a)))
        }

        test("apply-on-right-assignment-reclassifies-to-identity-when-substitution-matches") {
            val x = Var.of("X")
            val a = Atom.of("a")

            assertEquals(Equation.Identity(a, a), Equation.RightAssignment(a, x).apply(Substitution.of(x, a)))
        }

        test("apply-leaves-unrelated-assignment-unchanged") {
            val x = Var.of("X")
            val y = Var.of("Y")
            val a = Atom.of("a")

            assertEquals(Equation.LeftAssignment(x, a), Equation.LeftAssignment(x, a).apply(Substitution.of(y, a)))
        }

        test("apply-uses-provided-equality-checker-to-test-identity") {
            val x = Var.of("X")
            val a = Atom.of("a")

            val applied = Equation.LeftAssignment(x, a).apply(Substitution.of(x, a)) { _, _ -> false }

            assertFalse(applied.isIdentity)
            assertTrue(applied.isContradiction)
        }

        // endregion
        // region companion `of`
        test("of-classifies-equal-variables-as-identity") {
            val x = Var.of("X")
            assertEquals(Equation.Identity(x, x), Equation.of(x, x))
        }

        test("of-classifies-different-variables-as-left-assignment-never-right-assignment") {
            val x = Var.of("X")
            val y = Var.of("Y")
            assertEquals(Equation.LeftAssignment(x, y), Equation.of(x, y))
            assertEquals(Equation.LeftAssignment(y, x), Equation.of(y, x))
        }

        test("of-classifies-variable-and-term-with-variable-on-lhs-as-left-assignment") {
            val x = Var.of("X")
            val a = Atom.of("a")
            assertEquals(Equation.LeftAssignment(x, a), Equation.of(x, a))
        }

        test("of-classifies-variable-and-term-with-variable-on-rhs-as-right-assignment") {
            val x = Var.of("X")
            val a = Atom.of("a")
            assertEquals(Equation.RightAssignment(a, x), Equation.of(a, x))
        }

        test("of-classifies-equal-constants-as-identity") {
            assertEquals(Equation.Identity(Integer.of(1), Integer.of(1)), Equation.of(Integer.of(1), Integer.of(1)))
        }

        test("of-classifies-different-constants-as-contradiction") {
            assertEquals(
                Equation.Contradiction(Integer.of(1), Integer.of(2)),
                Equation.of(Integer.of(1), Integer.of(2)),
            )
        }

        test("of-classifies-constant-and-non-constant-non-var-term-as-contradiction") {
            val result = Equation.of(Integer.of(1), Struct.of("f", Atom.of("a")))
            assertTrue(result.isContradiction)
        }

        test("of-classifies-structs-with-matching-functor-and-arity-as-comparison") {
            val lhs = Struct.of("f", Var.of("X"))
            val rhs = Struct.of("f", Var.of("Y"))
            assertEquals(Equation.Comparison(lhs, rhs), Equation.of(lhs, rhs))
        }

        test("of-classifies-structs-with-mismatched-arity-as-contradiction") {
            val lhs = Struct.of("f", Var.of("X"))
            val rhs = Struct.of("f", Var.of("X"), Var.of("Y"))
            assertEquals(Equation.Contradiction(lhs, rhs), Equation.of(lhs, rhs))
        }

        test("of-classifies-structs-with-mismatched-functor-as-contradiction") {
            val lhs = Struct.of("f", Var.of("X"))
            val rhs = Struct.of("g", Var.of("X"))
            assertEquals(Equation.Contradiction(lhs, rhs), Equation.of(lhs, rhs))
        }

        test("of-pair-delegates-to-of-with-lhs-and-rhs") {
            val x = Var.of("X")
            val a = Atom.of("a")
            assertEquals(Equation.of(x, a), Equation.of(x to a))
            assertEquals(Equation.of(a, x), Equation.of(a to x))
        }

        test("of-with-always-false-equality-checker-never-produces-identity") {
            assertFalse(Equation.of(Atom.of("a"), Atom.of("a")) { _, _ -> false }.isIdentity)
            assertTrue(Equation.of(Var.of("X"), Var.of("X")) { _, _ -> false }.isLeftAssignment)
        }

        test("of-with-always-true-equality-checker-only-affects-var-var-and-constant-constant-branches") {
            assertTrue(Equation.of(Integer.of(1), Integer.of(2)) { _, _ -> true }.isIdentity)
            assertTrue(Equation.of(Var.of("X"), Var.of("Y")) { _, _ -> true }.isIdentity)
            // struct arity/functor mismatch and struct/struct comparisons never consult the equality checker
            assertTrue(
                Equation
                    .of(
                        Struct.of("f", Atom.of("a")),
                        Struct.of("g", Atom.of("a")),
                    ) { _, _ -> true }
                    .isContradiction,
            )
            assertTrue(
                Equation.of(Struct.of("f", Var.of("X")), Struct.of("f", Var.of("Y"))) { _, _ -> true }.isComparison,
            )
        }

        // endregion
        // region companion `from`
        test("from-sequence-flat-maps-all-of-over-every-pair") {
            val pairs = sequenceOf(Var.of("X") to Atom.of("a"), Atom.of("a") to Atom.of("a"))
            assertEquals(pairs.flatMap { Equation.allOf(it) }.toList(), Equation.from(pairs).toList())
        }

        test("from-iterable-and-vararg-delegate-to-from-sequence") {
            val pairList = listOf(Var.of("X") to Atom.of("a"), Atom.of("a") to Atom.of("a"))
            val expected = Equation.from(pairList.asSequence()).toList()

            assertEquals(expected, Equation.from(pairList).toList())
            assertEquals(expected, Equation.from(*pairList.toTypedArray()).toList())
        }

        // endregion
        // region companion `allOf`
        test("all-of-on-atoms-produces-a-single-equation") {
            val a = Atom.of("a")
            assertEquals(listOf(Equation.Identity(a, a)), Equation.allOf(a, a).toList())
        }

        test("all-of-on-proper-lists-compares-elements-pairwise-and-terminators-identically") {
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

        test("all-of-on-lists-with-mismatched-length-yields-a-contradiction") {
            val lhs = LogicList.of(Atom.of("a"))
            val rhs = LogicList.of(Atom.of("a"), Atom.of("b"))

            assertAnyContradiction(Equation.allOf(lhs, rhs))
        }

        test("all-of-on-improper-lists-compares-heads-and-delegates-on-var-tails") {
            val v = Var.of("V")
            val w = Var.of("W")
            val lhs = Cons.of(Atom.of("a"), v)
            val rhs = Cons.of(Atom.of("a"), w)

            assertEquals(
                listOf(Equation.Identity(Atom.of("a"), Atom.of("a")), Equation.LeftAssignment(v, w)),
                Equation.allOf(lhs, rhs).toList(),
            )
        }

        test("all-of-on-tuples-compares-left-elements-pairwise") {
            val x = Var.of("X")
            val y = Var.of("Y")
            val lhs = Tuple.of(Atom.of("a"), x)
            val rhs = Tuple.of(Atom.of("a"), y)

            assertEquals(
                listOf(Equation.Identity(Atom.of("a"), Atom.of("a")), Equation.LeftAssignment(x, y)),
                Equation.allOf(lhs, rhs).toList(),
            )
        }

        test("all-of-on-structs-with-matching-functor-and-arity-recurses-into-arguments") {
            val x = Var.of("X")
            val y = Var.of("Y")
            val lhs = Struct.of("f", Atom.of("a"), x)
            val rhs = Struct.of("f", Atom.of("a"), y)

            assertEquals(
                listOf(Equation.Identity(Atom.of("a"), Atom.of("a")), Equation.LeftAssignment(x, y)),
                Equation.allOf(lhs, rhs).toList(),
            )
        }

        test("all-of-on-structs-with-mismatched-arity-or-functor-yields-a-single-contradiction") {
            val mismatchedArity = Equation.allOf(Struct.of("f", Var.of("X")), Struct.of("f", Var.of("X"), Var.of("Y")))
            val mismatchedFunctor = Equation.allOf(Struct.of("f", Var.of("X")), Struct.of("g", Var.of("X")))

            assertEquals(1, mismatchedArity.count())
            assertTrue(mismatchedArity.single().isContradiction)
            assertEquals(1, mismatchedFunctor.count())
            assertTrue(mismatchedFunctor.single().isContradiction)
        }

        test("all-of-never-produces-a-bare-comparison-even-when-of-would") {
            val lhs = Struct.of("f", Var.of("X"))
            val rhs = Struct.of("f", Var.of("Y"))

            assertTrue(Equation.of(lhs, rhs).isComparison)
            assertNoComparisons(Equation.allOf(lhs, rhs))
        }

        test("all-of-pair-delegates-to-all-of-with-lhs-and-rhs") {
            val lhs = Struct.of("f", Var.of("X"))
            val rhs = Struct.of("f", Var.of("Y"))
            assertEquals(Equation.allOf(lhs, rhs).toList(), Equation.allOf(lhs to rhs).toList())
        }

        test("all-of-uses-provided-equality-checker-for-leaf-equations") {
            assertNoIdentities(
                Equation.allOf(Struct.of("f", Atom.of("a")), Struct.of("f", Atom.of("a"))) { _, _ -> false },
            )
        }

        // endregion
        // region bulk checks against EquationUtils fixtures
        test("of-classifies-all-shallow-fixtures-correctly") {
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

        test("of-preserves-assignment-variable-term-mapping-regardless-of-orientation") {
            val expected = EquationUtils.assignmentEquations.map { Equation.of(it).toAssignmentPair() }
            val actual = EquationUtils.assignmentEquationsShuffled.map { Equation.of(it).toAssignmentPair() }

            assertEquals(expected, actual)
        }

        test("all-of-classifies-all-fixtures-correctly") {
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

        test("all-of-produces-one-equation-per-leaf-for-identity-fixtures") {
            val expected = EquationUtils.allIdentityEquations.sumOf { (lhs, _) -> countDeepGeneratedEquations(lhs) }
            val actual = EquationUtils.allIdentityEquations.sumOf { Equation.allOf(it).count() }

            assertEquals(expected, actual)
        }

        test("all-of-preserves-assignment-variable-term-mapping-regardless-of-orientation") {
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

        test("all-of-never-exposes-bare-comparison-across-all-fixtures") {
            EquationUtils.mixedShuffledAllEquations.map { Equation.allOf(it) }.forEach(::assertNoComparisons)
        }

        test("swap-preserves-assignment-variable-term-mapping-for-non-var-to-var-assignments") {
            val nonVarToVarAssignments =
                EquationUtils.assignmentEquations.filterNot { (lhs, rhs) ->
                    lhs.isVar && rhs.isVar
                }

            val expected = nonVarToVarAssignments.map { Equation.of(it).toAssignmentPair() }
            val actual = nonVarToVarAssignments.map { Equation.of(it).swap().toAssignmentPair() }

            assertEquals(expected, actual)
        }

        test("swap-can-invert-identity-comparison-and-contradiction-fixtures") {
            val testableItems =
                EquationUtils.allIdentityEquations + EquationUtils.allContradictionEquations +
                    EquationUtils.comparisonEquations

            val expected = testableItems.map { (lhs, rhs) -> rhs to lhs }.map { Equation.of(it) }
            val actual = testableItems.map { Equation.of(it) }.map(Equation::swap)

            assertEquals(expected, actual)
        }

        test("of-uses-provided-equality-checker-across-all-fixtures") {
            assertNoIdentities(EquationUtils.mixedAllEquations.map { Equation.of(it) { _, _ -> false } }.asSequence())
        }

        test("all-of-uses-provided-equality-checker-across-all-fixtures") {
            assertNoIdentities(
                EquationUtils.mixedAllEquations
                    .flatMap {
                        Equation
                            .allOf(
                                it,
                            ) { _, _ -> false }
                            .asIterable()
                    }.asSequence(),
            )
        }

        test("equation-to-substitution-matches-substitution-built-from-raw-pairs") {
            val expected = EquationUtils.assignmentEquations.map { Substitution.of(it) }
            val actual = EquationUtils.assignmentEquations.map { Equation.of(it).toSubstitution() }

            assertEquals(expected, actual)
        }

        // endregion
        // region UnificationUtils extensions
        test("eq-infix-delegates-to-equation-of") {
            val x = Var.of("X")
            val a = Atom.of("a")
            assertEquals(Equation.of(x, a), x eq a)
            assertEquals(Equation.of(a, x), a eq x)
        }

        test("iterable-of-equations-to-substitution-merges-all-assignments") {
            val equations = EquationUtils.assignmentEquations.map { Equation.of(it) }
            assertEquals(Substitution.of(EquationUtils.assignmentEquations), equations.toSubstitution())
        }

        test("substitution-to-equations-always-produces-left-assignments") {
            val substitution = Substitution.of(EquationUtils.assignmentEquations)
            val equations = substitution.toEquations()

            assertTrue(equations.all { it.isLeftAssignment })
            assertEquals(EquationUtils.assignmentEquations.map { Equation.of(it) }, equations)
        }
    })
