package it.unibo.tuprolog.solve

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Truth
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

            toBeTested.query shouldBe aQuery
            toBeTested.substitution shouldBe aSubstitution
        }

        test("yes-solution-substitution-defaults-to-empty-if-not-specified") {
            val toBeTested = Solution.yes(aQuery)

            toBeTested.substitution shouldBe Substitution.empty()
        }

        test("yes-solution-computes-solved-query-applying-substitution-to-query") {
            val toBeTested = Solution.yes(aQuery, aSubstitution)

            toBeTested.solvedQuery shouldBe theQuerySolved
        }

        test("yes-solution-secondary-constructor-works-as-expected") {
            val toBeTested = Solution.yes(querySignature, queryArgList, aSubstitution)

            toBeTested.query shouldBe aQuery
            toBeTested.substitution shouldBe aSubstitution
            toBeTested.solvedQuery shouldBe theQuerySolved
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
            Solution.no(aQuery).query shouldBe aQuery
        }

        test("no-solution-solution-always-null") {
            Solution.no(aQuery).solvedQuery.shouldBeNull()
        }

        test("no-solution-substitution-always-failed") {
            Solution.no(aQuery).substitution shouldBe Substitution.failed()
        }

        test("no-solution-secondary-constructor-works-as-expected") {
            val toBeTested = Solution.no(querySignature, queryArgList)

            toBeTested.query shouldBe aQuery
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

            toBeTested.query shouldBe aQuery
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

            toBeTested.query shouldBe aQuery
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
    })
