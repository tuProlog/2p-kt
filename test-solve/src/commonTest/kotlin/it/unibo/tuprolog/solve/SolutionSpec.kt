package it.unibo.tuprolog.solve

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Goal
import it.unibo.tuprolog.core.Pattern
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.testutils.SolutionUtils.aQuery
import it.unibo.tuprolog.solve.testutils.SolutionUtils.aSubstitution
import it.unibo.tuprolog.solve.testutils.SolutionUtils.anException
import it.unibo.tuprolog.solve.testutils.SolutionUtils.queryArgList
import it.unibo.tuprolog.solve.testutils.SolutionUtils.querySignature
import it.unibo.tuprolog.solve.testutils.SolutionUtils.theQuerySolved

/** One kind of solution, and the name [Solution.whenIs] must dispatch it to. */
private class KindCase(
    val id: String,
    val solution: () -> Solution,
)

private val kinds =
    listOf(
        KindCase("yes") { Solution.yes(aQuery) },
        KindCase("no") { Solution.no(aQuery) },
        KindCase("halt") { Solution.halt(aQuery, anException) },
    )

private fun Solution.dispatched(): String = whenIs(yes = { "yes" }, no = { "no" }, halt = { "halt" })

/**
 * Test class for [Solution], [Solution.Yes], [Solution.No] and [Solution.Halt]
 *
 * @author Enrico
 */
class SolutionSpec :
    FunSpec({
        test("yes-solution-contains-inserted-data") {
            val toBeTested = Solution.yes(aQuery, aSubstitution)

            toBeTested.query shouldBe Goal.of(aQuery)
            toBeTested.substitution shouldBe aSubstitution
        }

        test("yes-solution-substitution-defaults-to-empty-if-not-specified") {
            val toBeTested = Solution.yes(aQuery)

            toBeTested.substitution shouldBe Substitution.empty()
        }

        test("yes-solution-computes-solved-query-applying-substitution-to-query") {
            val toBeTested = Solution.yes(aQuery, aSubstitution)

            toBeTested.solvedQuery shouldBe Goal.of(theQuerySolved)
        }

        test("yes-solution-secondary-constructor-works-as-expected") {
            val toBeTested = Solution.yes(querySignature, queryArgList, aSubstitution)

            toBeTested.query shouldBe Goal.of(aQuery)
            toBeTested.substitution shouldBe aSubstitution
            toBeTested.solvedQuery shouldBe Goal.of(theQuerySolved)
        }

        test("yes-solution-secondary-constructor-complains-if-arity-doesnt-match-arguments-count") {
            shouldThrow<IllegalArgumentException> {
                Solution.yes(
                    querySignature,
                    emptyList(),
                    Substitution.empty(),
                )
            }
            shouldThrow<IllegalArgumentException> {
                Solution.yes(querySignature, listOf(Truth.TRUE, Truth.TRUE), Substitution.empty())
            }
        }

        test("yes-solution-secondary-constructor-complains-if-signature-vararg") {
            shouldThrow<IllegalArgumentException> {
                Solution.yes(querySignature.copy(vararg = true), queryArgList, Substitution.empty())
            }
        }

        test("no-solution-contains-inserted-data") {
            Solution.no(aQuery).query shouldBe Goal.of(aQuery)
        }

        test("no-solution-solution-always-null") {
            Solution.no(aQuery).solvedQuery.shouldBeNull()
        }

        test("no-solution-substitution-always-failed") {
            Solution.no(aQuery).substitution shouldBe Substitution.failed()
        }

        test("no-solution-secondary-constructor-works-as-expected") {
            val toBeTested = Solution.no(querySignature, queryArgList)

            toBeTested.query shouldBe Goal.of(aQuery)
        }

        test("no-solution-secondary-constructor-complains-if-arity-doesnt-match-arguments-count") {
            shouldThrow<IllegalArgumentException> { Solution.no(querySignature, emptyList()) }
            shouldThrow<IllegalArgumentException> {
                Solution.no(querySignature, listOf(Truth.TRUE, Truth.TRUE))
            }
        }

        test("no-solution-secondary-constructor-complains-if-signature-vararg") {
            shouldThrow<IllegalArgumentException> {
                Solution.no(querySignature.copy(vararg = true), queryArgList)
            }
        }

        test("halt-solution-contains-inserted-data") {
            val toBeTested = Solution.halt(aQuery, anException)

            toBeTested.query shouldBe Goal.of(aQuery)
            toBeTested.exception shouldBe anException
        }

        test("halt-solution-solution-always-null") {
            Solution.halt(aQuery, anException).solvedQuery.shouldBeNull()
        }

        test("halt-solution-substitution-always-failed") {
            Solution.halt(aQuery, anException).substitution shouldBe Substitution.failed()
        }

        test("halt-solution-secondary-constructor-works-as-expected") {
            val toBeTested = Solution.halt(querySignature, queryArgList, anException)

            toBeTested.query shouldBe Goal.of(aQuery)
        }

        test("halt-solution-secondary-constructor-complains-if-arity-doesnt-match-arguments-count") {
            shouldThrow<IllegalArgumentException> { Solution.halt(querySignature, emptyList(), anException) }
            shouldThrow<IllegalArgumentException> {
                Solution.halt(querySignature, listOf(Truth.TRUE, Truth.TRUE), anException)
            }
        }

        test("halt-solution-secondary-constructor-complains-if-signature-vararg") {
            shouldThrow<IllegalArgumentException> {
                Solution.halt(querySignature.copy(vararg = true), queryArgList, anException)
            }
        }

        test("clean-up-keeps-wildcard-variables-by-default") {
            val scope = Scope.empty()
            val solution =
                with(scope) {
                    val query = Struct.of("f", varOf("X"), varOf("_P"))
                    val substitution = Substitution.of(mapOf(varOf("X") to Atom.of("x"), varOf("_P") to Atom.of("p")))
                    Solution.yes(query, substitution)
                }.cleanUp(hideWildcardVariables = false)

            solution.valueOf("X") shouldBe Atom.of("x")
            solution.valueOf("_P") shouldBe Atom.of("p")
        }

        test("clean-up-hides-wildcard-variables-when-asked") {
            val scope = Scope.empty()
            val solution =
                with(scope) {
                    val query = Struct.of("f", varOf("X"), varOf("_P"))
                    val substitution = Substitution.of(mapOf(varOf("X") to Atom.of("x"), varOf("_P") to Atom.of("p")))
                    Solution.yes(query, substitution)
                }.cleanUp(hideWildcardVariables = true)

            solution.valueOf("X") shouldBe Atom.of("x")
            solution.valueOf("_P").shouldBeNull()
        }

        test("clean-up-hiding-wildcard-variables-is-a-no-op-for-no-and-halt") {
            val no = Solution.no(aQuery).cleanUp(hideWildcardVariables = true)
            val halt = Solution.halt(aQuery, anException).cleanUp(hideWildcardVariables = true)

            no shouldBe Solution.no(aQuery)
            halt shouldBe Solution.halt(aQuery, anException)
        }

        withTests(nameFn = { "kind/${it.id}/casts-only-to-itself" }, kinds) { case ->
            val solution = case.solution()
            (solution.asYes() != null) shouldBe (case.id == "yes")
            (solution.asNo() != null) shouldBe (case.id == "no")
            (solution.asHalt() != null) shouldBe (case.id == "halt")
            if (case.id != "yes") shouldThrow<ClassCastException> { solution.castToYes() }
            if (case.id != "no") shouldThrow<ClassCastException> { solution.castToNo() }
            if (case.id != "halt") shouldThrow<ClassCastException> { solution.castToHalt() }
        }

        withTests(nameFn = { "kind/${it.id}/when-is-dispatches-to-its-branch" }, kinds) { case ->
            case.solution().dispatched() shouldBe case.id
        }

        withTests(nameFn = { "kind/${it.id}/when-is-falls-back-to-otherwise" }, kinds) { case ->
            val solution = case.solution()
            (solution.whenIs<String>(otherwise = { "otherwise" })) shouldBe "otherwise"
            shouldThrow<IllegalStateException> { solution.whenIs<String>() }
        }

        test("compound-queries-become-goals") {
            val x = Var.of("X")
            val call = Struct.of("p", x)
            Solution.yes(call).query shouldBe Goal.of(call)
            Solution.no(call).query shouldBe Goal.of(call)
            Solution.halt(call, anException).query shouldBe Goal.of(call)
        }

        test("goal-queries-are-not-wrapped-again") {
            val goal = Goal.of(Struct.of("p", Var.of("X")))
            Solution.yes(goal).query shouldBeSameInstanceAs goal
            Solution.no(goal).query shouldBeSameInstanceAs goal
        }

        test("solved-query-is-a-goal") {
            val x = Var.of("X")
            val solution = Solution.yes(Struct.of("p", x), Substitution.of(x, Atom.of("a")))
            solution.solvedQuery shouldBe Goal.of(Struct.of("p", Atom.of("a")))
        }

        test("solved-pattern-query-is-a-concrete-goal") {
            val f = Var.of("F")
            val x = Var.of("X")
            val solution = Solution.yes(Pattern.of(f, x), Substitution.of(mapOf(f to Atom.of("p"), x to Atom.of("a"))))
            solution.solvedQuery shouldBe Goal.of(Struct.of("p", Atom.of("a")))
        }

        test("solutions-of-a-call-and-of-its-goal-are-equal") {
            val x = Var.of("X")
            val call = Struct.of("p", x)
            val substitution = Substitution.of(x, Atom.of("a"))
            val ofCall = Solution.yes(call, substitution)
            val ofGoal = Solution.yes(Goal.of(call), substitution)
            ofCall shouldBe ofGoal
            ofCall.hashCode() shouldBe ofGoal.hashCode()
            Solution.no(call) shouldBe Solution.no(Goal.of(call))
        }

        test("copies-take-compound-queries") {
            val call = Struct.of("q", Var.of("Y"))
            Solution.yes(Atom.of("p")).copy(query = call).query shouldBe Goal.of(call)
            Solution.no(Atom.of("p")).copy(query = call).query shouldBe Goal.of(call)
        }

        test("clean-up-keeps-the-variables-of-the-goal") {
            val x = Var.of("X")
            val y = Var.of("Y")
            val solution =
                Solution.yes(
                    Goal.of(Struct.of("p", x)),
                    Substitution.of(mapOf(x to Atom.of("a"), y to Atom.of("b"))),
                )
            solution.cleanUp(hideWildcardVariables = false).substitution shouldBe Substitution.of(x, Atom.of("a"))
        }
    })
