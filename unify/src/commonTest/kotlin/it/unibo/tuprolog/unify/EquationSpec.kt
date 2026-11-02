package it.unibo.tuprolog.unify

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
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
import it.unibo.tuprolog.core.List as LogicList

/**
 * Test class for [Equation], its companion object, its subtypes, and the [UnificationUtils] extensions built on it
 */
class EquationSpec :
    FunSpec({
        val oneInstancePerType =
            listOf(
                EquationTypeCase("identity") { Equation.Identity(Atom.of("a"), Atom.of("a")) },
                EquationTypeCase("left-assignment") { Equation.LeftAssignment(Var.of("X"), Atom.of("a")) },
                EquationTypeCase("right-assignment") { Equation.RightAssignment(Atom.of("a"), Var.of("X")) },
                EquationTypeCase("comparison") {
                    Equation.Comparison(Struct.of("f", Var.of("X")), Struct.of("f", Var.of("Y")))
                },
                EquationTypeCase("contradiction") { Equation.Contradiction(Atom.of("a"), Atom.of("b")) },
            )

        fun assertOnlyDiscriminator(
            equation: Equation,
            isIdentity: Boolean = false,
            isAssignment: Boolean = false,
            isLeftAssignment: Boolean = false,
            isRightAssignment: Boolean = false,
            isComparison: Boolean = false,
            isContradiction: Boolean = false,
        ) {
            equation.isIdentity shouldBe isIdentity
            equation.isAssignment shouldBe isAssignment
            equation.isLeftAssignment shouldBe isLeftAssignment
            equation.isRightAssignment shouldBe isRightAssignment
            equation.isComparison shouldBe isComparison
            equation.isContradiction shouldBe isContradiction

            (equation.asIdentity() === equation) shouldBe isIdentity
            (equation.asAssignment() === equation) shouldBe isAssignment
            (equation.asLeftAssignment() === equation) shouldBe isLeftAssignment
            (equation.asRightAssignment() === equation) shouldBe isRightAssignment
            (equation.asComparison() === equation) shouldBe isComparison
            (equation.asContradiction() === equation) shouldBe isContradiction

            (runCatching { equation.castToIdentity() }.getOrNull() === equation) shouldBe isIdentity
            (runCatching { equation.castToAssignment() }.getOrNull() === equation) shouldBe isAssignment
            (runCatching { equation.castToLeftAssignment() }.getOrNull() === equation) shouldBe isLeftAssignment
            (runCatching { equation.castToRightAssignment() }.getOrNull() === equation) shouldBe isRightAssignment
            (runCatching { equation.castToComparison() }.getOrNull() === equation) shouldBe isComparison
            (runCatching { equation.castToContradiction() }.getOrNull() === equation) shouldBe isContradiction
        }

        // region construction
        test("identity-constructor-preserves-lhs-and-rhs") {
            val (lhs, rhs) = Atom.of("a") to Atom.of("a")
            (Equation.Identity(lhs, rhs).let { it.lhs to it.rhs }) shouldBe (lhs to rhs)
        }

        test("left-assignment-constructor-preserves-lhs-and-rhs") {
            val (lhs, rhs) = Var.of("X") to Atom.of("a")
            (Equation.LeftAssignment(lhs, rhs).let { it.lhs to it.rhs }) shouldBe (lhs to rhs)
        }

        test("right-assignment-constructor-preserves-lhs-and-rhs") {
            val (lhs, rhs) = Atom.of("a") to Var.of("X")
            (Equation.RightAssignment(lhs, rhs).let { it.lhs to it.rhs }) shouldBe (lhs to rhs)
        }

        test("comparison-constructor-preserves-lhs-and-rhs") {
            val (lhs, rhs) = Struct.of("f", Var.of("X")) to Struct.of("f", Var.of("Y"))
            (Equation.Comparison(lhs, rhs).let { it.lhs to it.rhs }) shouldBe (lhs to rhs)
        }

        test("contradiction-constructor-preserves-lhs-and-rhs") {
            val (lhs, rhs) = Atom.of("a") to Atom.of("b")
            (Equation.Contradiction(lhs, rhs).let { it.lhs to it.rhs }) shouldBe (lhs to rhs)
        }

        // endregion
        // region variable / term
        test("left-assignment-variable-and-term-mirror-lhs-and-rhs") {
            val variable = Var.of("X")
            val term = Atom.of("a")
            val equation = Equation.LeftAssignment(variable, term)

            equation.variable shouldBe variable
            equation.term shouldBe term
        }

        test("right-assignment-variable-and-term-mirror-rhs-and-lhs") {
            val variable = Var.of("X")
            val term = Atom.of("a")
            val equation = Equation.RightAssignment(term, variable)

            equation.variable shouldBe variable
            equation.term shouldBe term
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
        withTests(nameFn = { "clone-without-arguments/${it.id}" }, oneInstancePerType) { case ->
            val equation = case.equation()
            val cloned = equation.clone()
            cloned shouldBe equation
            cloned::class shouldBe equation::class
        }

        test("clone-with-new-arguments-replaces-lhs-and-rhs") {
            Equation.Identity(Atom.of("a"), Atom.of("a")).clone(Atom.of("b"), Atom.of("c")) shouldBe
                Equation.Identity(Atom.of("b"), Atom.of("c"))
            val newLeftVar = Var.of("Y")
            Equation.LeftAssignment(Var.of("X"), Atom.of("a")).clone(newLeftVar, Atom.of("b")) shouldBe
                Equation.LeftAssignment(newLeftVar, Atom.of("b"))
            val newRightVar = Var.of("Y")
            Equation.RightAssignment(Atom.of("a"), Var.of("X")).clone(Atom.of("b"), newRightVar) shouldBe
                Equation.RightAssignment(Atom.of("b"), newRightVar)
            Equation.Comparison(Atom.of("a"), Atom.of("a")).clone(Atom.of("b"), Atom.of("c")) shouldBe
                Equation.Comparison(Atom.of("b"), Atom.of("c"))
            Equation.Contradiction(Atom.of("a"), Atom.of("a")).clone(Atom.of("b"), Atom.of("c")) shouldBe
                Equation.Contradiction(Atom.of("b"), Atom.of("c"))
        }

        // endregion
        withTests(nameFn = { "to-contradiction/${it.id}" }, oneInstancePerType) { case ->
            val equation = case.equation()
            equation.toContradiction() shouldBe Equation.Contradiction(equation.lhs, equation.rhs)
        }

        // region toAssignmentPair
        test("to-assignment-pair-on-left-assignment-returns-variable-and-term") {
            val variable = Var.of("X")
            val term = Atom.of("a")
            Equation.LeftAssignment(variable, term).toAssignmentPair() shouldBe (variable to term)
        }

        test("to-assignment-pair-on-right-assignment-returns-variable-and-term") {
            val variable = Var.of("X")
            val term = Atom.of("a")
            Equation.RightAssignment(term, variable).toAssignmentPair() shouldBe (variable to term)
        }

        test("to-assignment-pair-on-non-assignment-falls-back-to-whichever-side-is-a-variable") {
            val variable = Var.of("X")
            val term = Atom.of("a")
            Equation.Identity(variable, term).toAssignmentPair() shouldBe (variable to term)
            Equation.Comparison(term, variable).toAssignmentPair() shouldBe (variable to term)
        }

        test("to-assignment-pair-throws-when-neither-side-is-a-variable") {
            shouldThrow<IllegalArgumentException> {
                Equation.Contradiction(Atom.of("a"), Atom.of("b")).toAssignmentPair()
            }
            shouldThrow<IllegalArgumentException> {
                Equation.Comparison(Atom.of("a"), Atom.of("a")).toAssignmentPair()
            }
        }

        // endregion
        // region toSubstitution
        test("to-substitution-on-left-assignment-binds-variable-to-term") {
            val variable = Var.of("X")
            val term = Atom.of("a")
            Equation.LeftAssignment(variable, term).toSubstitution() shouldBe Substitution.of(variable, term)
        }

        test("to-substitution-on-right-assignment-binds-variable-to-term") {
            val variable = Var.of("X")
            val term = Atom.of("a")
            Equation.RightAssignment(term, variable).toSubstitution() shouldBe Substitution.of(variable, term)
        }

        test("to-substitution-on-contradiction-is-always-failed-regardless-of-content") {
            Equation
                .Contradiction(Atom.of("a"), Atom.of("b"))
                .toSubstitution()
                .isFailed
                .shouldBeTrue()
            Equation
                .Contradiction(Var.of("X"), Var.of("X"))
                .toSubstitution()
                .isFailed
                .shouldBeTrue()
        }

        test("to-substitution-on-non-assignment-falls-back-to-whichever-side-is-a-variable") {
            val variable = Var.of("X")
            val term = Atom.of("a")
            Equation.Identity(variable, term).toSubstitution() shouldBe Substitution.of(variable, term)
        }

        test("to-substitution-throws-when-neither-side-is-a-variable") {
            shouldThrow<IllegalArgumentException> {
                Equation.Comparison(Atom.of("a"), Atom.of("a")).toSubstitution()
            }
        }

        // endregion
        withTests(nameFn = { "to-term/${it.id}" }, oneInstancePerType) { case ->
            val equation = case.equation()
            equation.toTerm() shouldBe Struct.of("=", equation.lhs, equation.rhs)
        }

        // region toPair
        test("to-pair-on-base-equation-types-returns-lhs-then-rhs") {
            val a = Atom.of("a")
            val b = Atom.of("b")
            Equation.Identity(a, b).toPair() shouldBe (a to b)
            Equation.Comparison(a, b).toPair() shouldBe (a to b)
            Equation.Contradiction(a, b).toPair() shouldBe (a to b)
        }

        test("to-pair-on-left-assignment-returns-variable-then-term") {
            val variable = Var.of("X")
            val term = Atom.of("a")
            Equation.LeftAssignment(variable, term).toPair() shouldBe (variable to term)
        }

        test("to-pair-on-right-assignment-returns-term-then-variable") {
            val variable = Var.of("X")
            val term = Atom.of("a")
            Equation.RightAssignment(term, variable).toPair() shouldBe (term to variable)
        }

        // endregion
        // region swap
        test("swap-inverts-identity-comparison-and-contradiction") {
            // two differently-formatted but numerically-equal terms, so swap must reclassify both orderings as Identity
            val real1 = Real.of("1.5")
            val real2 = Real.of("1.500")
            Equation.Identity(real1, real2).swap() shouldBe Equation.Identity(real2, real1)

            val a = Atom.of("a")
            val b = Atom.of("b")
            Equation.Contradiction(a, b).swap() shouldBe Equation.Contradiction(b, a)

            val x = Var.of("X")
            val y = Var.of("Y")
            Equation.Comparison(Struct.of("f", x), Struct.of("f", y)).swap() shouldBe
                Equation.Comparison(Struct.of("f", y), Struct.of("f", x))
        }

        test("swap-turns-left-assignment-into-right-assignment-preserving-variable-and-term") {
            val variable = Var.of("X")
            val term = Atom.of("a")
            val swapped = Equation.LeftAssignment(variable, term).swap()

            swapped shouldBe Equation.RightAssignment(term, variable)
            swapped.toAssignmentPair() shouldBe (variable to term)
        }

        test("swap-turns-right-assignment-into-left-assignment-preserving-variable-and-term") {
            val variable = Var.of("X")
            val term = Atom.of("a")
            val swapped = Equation.RightAssignment(term, variable).swap()

            swapped shouldBe Equation.LeftAssignment(variable, term)
            swapped.toAssignmentPair() shouldBe (variable to term)
        }

        test("swap-on-var-to-var-assignment-always-produces-left-assignment") {
            val x = Var.of("X")
            val y = Var.of("Y")
            val swapped = Equation.LeftAssignment(x, y).swap()

            swapped shouldBe Equation.LeftAssignment(y, x)
        }

        withTests(nameFn = { "double-swap/${it.id}" }, oneInstancePerType) { case ->
            val equation = case.equation()
            equation.swap().swap() shouldBe equation
        }

        // endregion
        // region apply
        test("apply-on-left-assignment-reclassifies-to-identity-when-substitution-matches") {
            val x = Var.of("X")
            val a = Atom.of("a")

            Equation.LeftAssignment(x, a).apply(Substitution.of(x, a)) shouldBe Equation.Identity(a, a)
        }

        test("apply-on-right-assignment-reclassifies-to-identity-when-substitution-matches") {
            val x = Var.of("X")
            val a = Atom.of("a")

            Equation.RightAssignment(a, x).apply(Substitution.of(x, a)) shouldBe Equation.Identity(a, a)
        }

        test("apply-leaves-unrelated-assignment-unchanged") {
            val x = Var.of("X")
            val y = Var.of("Y")
            val a = Atom.of("a")

            Equation.LeftAssignment(x, a).apply(Substitution.of(y, a)) shouldBe Equation.LeftAssignment(x, a)
        }

        test("apply-uses-provided-equality-checker-to-test-identity") {
            val x = Var.of("X")
            val a = Atom.of("a")

            val applied = Equation.LeftAssignment(x, a).apply(Substitution.of(x, a)) { _, _ -> false }

            applied.isIdentity.shouldBeFalse()
            applied.isContradiction.shouldBeTrue()
        }

        // endregion
        // region companion `of`
        test("of-classifies-equal-variables-as-identity") {
            val x = Var.of("X")
            Equation.of(x, x) shouldBe Equation.Identity(x, x)
        }

        test("of-classifies-different-variables-as-left-assignment-never-right-assignment") {
            val x = Var.of("X")
            val y = Var.of("Y")
            Equation.of(x, y) shouldBe Equation.LeftAssignment(x, y)
            Equation.of(y, x) shouldBe Equation.LeftAssignment(y, x)
        }

        test("of-classifies-variable-and-term-with-variable-on-lhs-as-left-assignment") {
            val x = Var.of("X")
            val a = Atom.of("a")
            Equation.of(x, a) shouldBe Equation.LeftAssignment(x, a)
        }

        test("of-classifies-variable-and-term-with-variable-on-rhs-as-right-assignment") {
            val x = Var.of("X")
            val a = Atom.of("a")
            Equation.of(a, x) shouldBe Equation.RightAssignment(a, x)
        }

        test("of-classifies-equal-constants-as-identity") {
            Equation.of(Integer.of(1), Integer.of(1)) shouldBe Equation.Identity(Integer.of(1), Integer.of(1))
        }

        test("of-classifies-different-constants-as-contradiction") {
            Equation.of(Integer.of(1), Integer.of(2)) shouldBe Equation.Contradiction(Integer.of(1), Integer.of(2))
        }

        test("of-classifies-constant-and-non-constant-non-var-term-as-contradiction") {
            val result = Equation.of(Integer.of(1), Struct.of("f", Atom.of("a")))
            result.isContradiction.shouldBeTrue()
        }

        test("of-classifies-structs-with-matching-functor-and-arity-as-comparison") {
            val lhs = Struct.of("f", Var.of("X"))
            val rhs = Struct.of("f", Var.of("Y"))
            Equation.of(lhs, rhs) shouldBe Equation.Comparison(lhs, rhs)
        }

        test("of-classifies-structs-with-mismatched-arity-as-contradiction") {
            val lhs = Struct.of("f", Var.of("X"))
            val rhs = Struct.of("f", Var.of("X"), Var.of("Y"))
            Equation.of(lhs, rhs) shouldBe Equation.Contradiction(lhs, rhs)
        }

        test("of-classifies-structs-with-mismatched-functor-as-contradiction") {
            val lhs = Struct.of("f", Var.of("X"))
            val rhs = Struct.of("g", Var.of("X"))
            Equation.of(lhs, rhs) shouldBe Equation.Contradiction(lhs, rhs)
        }

        test("of-pair-delegates-to-of-with-lhs-and-rhs") {
            val x = Var.of("X")
            val a = Atom.of("a")
            Equation.of(x to a) shouldBe Equation.of(x, a)
            Equation.of(a to x) shouldBe Equation.of(a, x)
        }

        test("of-with-always-false-equality-checker-never-produces-identity") {
            (Equation.of(Atom.of("a"), Atom.of("a")) { _, _ -> false }.isIdentity).shouldBeFalse()
            (Equation.of(Var.of("X"), Var.of("X")) { _, _ -> false }.isLeftAssignment).shouldBeTrue()
        }

        test("of-with-always-true-equality-checker-only-affects-var-var-and-constant-constant-branches") {
            (Equation.of(Integer.of(1), Integer.of(2)) { _, _ -> true }.isIdentity).shouldBeTrue()
            (Equation.of(Var.of("X"), Var.of("Y")) { _, _ -> true }.isIdentity).shouldBeTrue()
            // struct arity/functor mismatch and struct/struct comparisons never consult the equality checker
            (
                Equation
                    .of(
                        Struct.of("f", Atom.of("a")),
                        Struct.of("g", Atom.of("a")),
                    ) { _, _ -> true }
                    .isContradiction
            ).shouldBeTrue()
            (
                Equation
                    .of(
                        Struct.of("f", Var.of("X")),
                        Struct.of("f", Var.of("Y")),
                    ) { _, _ -> true }
                    .isComparison
            ).shouldBeTrue()
        }

        // endregion
        // region companion `from`
        test("from-sequence-flat-maps-all-of-over-every-pair") {
            val pairs = sequenceOf(Var.of("X") to Atom.of("a"), Atom.of("a") to Atom.of("a"))
            Equation.from(pairs).toList() shouldBe (pairs.flatMap { Equation.allOf(it) }.toList())
        }

        test("from-iterable-and-vararg-delegate-to-from-sequence") {
            val pairList = listOf(Var.of("X") to Atom.of("a"), Atom.of("a") to Atom.of("a"))
            val expected = Equation.from(pairList.asSequence()).toList()

            Equation.from(pairList).toList() shouldBe expected
            Equation.from(*pairList.toTypedArray()).toList() shouldBe expected
        }

        // endregion
        // region companion `allOf`
        test("all-of-on-atoms-produces-a-single-equation") {
            val a = Atom.of("a")
            Equation.allOf(a, a).toList() shouldBe listOf(Equation.Identity(a, a))
        }

        test("all-of-on-proper-lists-compares-elements-pairwise-and-terminators-identically") {
            val x = Var.of("X")
            val y = Var.of("Y")
            val lhs = LogicList.of(Atom.of("a"), x)
            val rhs = LogicList.of(Atom.of("a"), y)

            Equation.allOf(lhs, rhs).toList() shouldBe
                listOf(
                    Equation.Identity(Atom.of("a"), Atom.of("a")),
                    Equation.LeftAssignment(x, y),
                    Equation.Identity(Empty.list(), Empty.list()),
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

            Equation.allOf(lhs, rhs).toList() shouldBe
                listOf(Equation.Identity(Atom.of("a"), Atom.of("a")), Equation.LeftAssignment(v, w))
        }

        test("all-of-on-tuples-compares-left-elements-pairwise") {
            val x = Var.of("X")
            val y = Var.of("Y")
            val lhs = Tuple.of(Atom.of("a"), x)
            val rhs = Tuple.of(Atom.of("a"), y)

            Equation.allOf(lhs, rhs).toList() shouldBe
                listOf(Equation.Identity(Atom.of("a"), Atom.of("a")), Equation.LeftAssignment(x, y))
        }

        test("all-of-on-structs-with-matching-functor-and-arity-recurses-into-arguments") {
            val x = Var.of("X")
            val y = Var.of("Y")
            val lhs = Struct.of("f", Atom.of("a"), x)
            val rhs = Struct.of("f", Atom.of("a"), y)

            Equation.allOf(lhs, rhs).toList() shouldBe
                listOf(Equation.Identity(Atom.of("a"), Atom.of("a")), Equation.LeftAssignment(x, y))
        }

        test("all-of-on-structs-with-mismatched-arity-or-functor-yields-a-single-contradiction") {
            val mismatchedArity = Equation.allOf(Struct.of("f", Var.of("X")), Struct.of("f", Var.of("X"), Var.of("Y")))
            val mismatchedFunctor = Equation.allOf(Struct.of("f", Var.of("X")), Struct.of("g", Var.of("X")))

            mismatchedArity.count() shouldBe 1
            mismatchedArity.single().isContradiction.shouldBeTrue()
            mismatchedFunctor.count() shouldBe 1
            mismatchedFunctor.single().isContradiction.shouldBeTrue()
        }

        test("all-of-never-produces-a-bare-comparison-even-when-of-would") {
            val lhs = Struct.of("f", Var.of("X"))
            val rhs = Struct.of("f", Var.of("Y"))

            Equation.of(lhs, rhs).isComparison.shouldBeTrue()
            assertNoComparisons(Equation.allOf(lhs, rhs))
        }

        test("all-of-pair-delegates-to-all-of-with-lhs-and-rhs") {
            val lhs = Struct.of("f", Var.of("X"))
            val rhs = Struct.of("f", Var.of("Y"))
            Equation.allOf(lhs to rhs).toList() shouldBe Equation.allOf(lhs, rhs).toList()
        }

        test("all-of-uses-provided-equality-checker-for-leaf-equations") {
            assertNoIdentities(
                Equation.allOf(Struct.of("f", Atom.of("a")), Struct.of("f", Atom.of("a"))) { _, _ -> false },
            )
        }

        // endregion
        // region bulk checks against EquationUtils fixtures
        withTests(
            nameFn = { "of-classifies-shallow-fixtures/${it.id}" },
            EquationUtils.shallowIdentityEquations,
        ) { case ->
            Equation.of(case.pair) shouldBe Equation.Identity(case.lhs, case.rhs)
        }

        withTests(
            nameFn = { "of-classifies-shallow-fixtures/${it.id}" },
            EquationUtils.assignmentEquationsShuffled,
        ) { case ->
            val expected =
                if (case.lhs.isVar) {
                    Equation.LeftAssignment(case.lhs.castToVar(), case.rhs)
                } else {
                    Equation.RightAssignment(case.lhs, case.rhs.castToVar())
                }
            Equation.of(case.pair) shouldBe expected
        }

        withTests(nameFn = { "of-classifies-shallow-fixtures/${it.id}" }, EquationUtils.comparisonEquations) { case ->
            Equation.of(case.pair) shouldBe Equation.Comparison(case.lhs, case.rhs)
        }

        withTests(
            nameFn = { "of-classifies-shallow-fixtures/${it.id}" },
            EquationUtils.shallowContradictionEquations,
        ) { case ->
            Equation.of(case.pair) shouldBe Equation.Contradiction(case.lhs, case.rhs)
        }

        withTests(
            nameFn = { "of-preserves-assignment-mapping/${it.id}" },
            EquationUtils.assignmentEquationsShuffled,
        ) { case ->
            val original = if (case.lhs.isVar) case else case.swapped()
            Equation.of(case.pair).toAssignmentPair() shouldBe Equation.of(original.pair).toAssignmentPair()
        }

        withTests(nameFn = { "all-of-classifies-fixtures/${it.id}" }, EquationUtils.allIdentityEquations) { case ->
            assertAllIdentities(Equation.allOf(case.pair))
        }

        withTests(
            nameFn = { "all-of-classifies-fixtures/${it.id}" },
            EquationUtils.assignmentEquationsShuffled,
        ) { case ->
            val eqSequence = Equation.allOf(case.pair)
            assertAnyAssignment(eqSequence)
            assertNoComparisons(eqSequence)
        }

        withTests(nameFn = { "all-of-classifies-fixtures/${it.id}" }, EquationUtils.comparisonEquations) { case ->
            assertNoComparisons(Equation.allOf(case.pair))
        }

        withTests(nameFn = { "all-of-classifies-fixtures/${it.id}" }, EquationUtils.allContradictionEquations) { case ->
            val eqSequence = Equation.allOf(case.pair)
            assertAnyContradiction(eqSequence)
            assertNoComparisons(eqSequence)
        }

        withTests(
            nameFn = { "all-of-produces-one-equation-per-leaf/${it.id}" },
            EquationUtils.allIdentityEquations,
        ) { case ->
            Equation.allOf(case.pair).count() shouldBe countDeepGeneratedEquations(case.lhs)
        }

        withTests(
            nameFn = { "all-of-preserves-assignment-mapping/${it.id}" },
            EquationUtils.assignmentEquationsShuffled,
        ) { case ->
            val original = if (case.lhs.isVar) case else case.swapped()
            (Equation.allOf(case.pair).map { it.toAssignmentPair() }.toList()) shouldBe
                (Equation.allOf(original.pair).map { it.toAssignmentPair() }.toList())
        }

        withTests(nameFn = {
            "all-of-never-exposes-bare-comparison/${it.id}"
        }, EquationUtils.mixedShuffledAllEquations) { case ->
            assertNoComparisons(Equation.allOf(case.pair))
        }

        withTests(
            nameFn = { "swap-preserves-assignment-mapping/${it.id}" },
            EquationUtils.assignmentEquations.filterNot { it.lhs.isVar && it.rhs.isVar },
        ) { case ->
            Equation.of(case.pair).swap().toAssignmentPair() shouldBe Equation.of(case.pair).toAssignmentPair()
        }

        withTests(
            nameFn = { "swap-inverts-fixtures/${it.id}" },
            EquationUtils.allIdentityEquations + EquationUtils.allContradictionEquations +
                EquationUtils.comparisonEquations,
        ) { case ->
            Equation.of(case.pair).swap() shouldBe Equation.of(case.rhs to case.lhs)
        }

        withTests(nameFn = { "of-uses-provided-equality-checker/${it.id}" }, EquationUtils.mixedAllEquations) { case ->
            assertNoIdentities(sequenceOf(Equation.of(case.pair) { _, _ -> false }))
        }

        withTests(
            nameFn = { "all-of-uses-provided-equality-checker/${it.id}" },
            EquationUtils.mixedAllEquations,
        ) { case ->
            assertNoIdentities(Equation.allOf(case.pair) { _, _ -> false })
        }

        withTests(nameFn = { "to-substitution/${it.id}" }, EquationUtils.assignmentEquations) { case ->
            Equation.of(case.pair).toSubstitution() shouldBe Substitution.of(case.pair)
        }

        // endregion
        // region UnificationUtils extensions
        test("eq-infix-delegates-to-equation-of") {
            val x = Var.of("X")
            val a = Atom.of("a")
            (x eq a) shouldBe Equation.of(x, a)
            (a eq x) shouldBe Equation.of(a, x)
        }

        test("iterable-of-equations-to-substitution-merges-all-assignments") {
            val pairs = EquationUtils.assignmentEquations.map { it.pair }
            (pairs.map { Equation.of(it) }.toSubstitution()) shouldBe Substitution.of(pairs)
        }

        test("substitution-to-equations-always-produces-left-assignments") {
            val pairs = EquationUtils.assignmentEquations.map { it.pair }
            val equations = Substitution.of(pairs).toEquations()

            (equations.all { it.isLeftAssignment }).shouldBeTrue()
            equations shouldBe (pairs.map { Equation.of(it) })
        }
    })

private class EquationTypeCase(
    val id: String,
    val equation: () -> Equation,
)
