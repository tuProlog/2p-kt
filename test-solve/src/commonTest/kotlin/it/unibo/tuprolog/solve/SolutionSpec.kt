package it.unibo.tuprolog.solve

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
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
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

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

            assertEquals(aQuery, toBeTested.query)
            assertEquals(aSubstitution, toBeTested.substitution)
        }

        test("yes-solution-substitution-defaults-to-empty-if-not-specified") {
            val toBeTested = Solution.yes(aQuery)

            assertEquals(Substitution.empty(), toBeTested.substitution)
        }

        test("yes-solution-computes-solved-query-applying-substitution-to-query") {
            val toBeTested = Solution.yes(aQuery, aSubstitution)

            assertEquals(theQuerySolved, toBeTested.solvedQuery)
        }

        test("yes-solution-secondary-constructor-works-as-expected") {
            val toBeTested = Solution.yes(querySignature, queryArgList, aSubstitution)

            assertEquals(aQuery, toBeTested.query)
            assertEquals(aSubstitution, toBeTested.substitution)
            assertEquals(theQuerySolved, toBeTested.solvedQuery)
        }

        test("yes-solution-secondary-constructor-complains-if-arity-doesnt-match-arguments-count") {
            assertFailsWith<IllegalArgumentException> {
                Solution.yes(
                    querySignature,
                    emptyList(),
                    Substitution.empty(),
                )
            }
            assertFailsWith<IllegalArgumentException> {
                Solution.yes(querySignature, listOf(Truth.TRUE, Truth.TRUE), Substitution.empty())
            }
        }

        test("yes-solution-secondary-constructor-complains-if-signature-vararg") {
            assertFailsWith<IllegalArgumentException> {
                Solution.yes(querySignature.copy(vararg = true), queryArgList, Substitution.empty())
            }
        }

        test("no-solution-contains-inserted-data") {
            assertEquals(aQuery, Solution.no(aQuery).query)
        }

        test("no-solution-solution-always-null") {
            assertNull(Solution.no(aQuery).solvedQuery)
        }

        test("no-solution-substitution-always-failed") {
            assertEquals(Substitution.failed(), Solution.no(aQuery).substitution)
        }

        test("no-solution-secondary-constructor-works-as-expected") {
            val toBeTested = Solution.no(querySignature, queryArgList)

            assertEquals(aQuery, toBeTested.query)
        }

        test("no-solution-secondary-constructor-complains-if-arity-doesnt-match-arguments-count") {
            assertFailsWith<IllegalArgumentException> { Solution.no(querySignature, emptyList()) }
            assertFailsWith<IllegalArgumentException> {
                Solution.no(querySignature, listOf(Truth.TRUE, Truth.TRUE))
            }
        }

        test("no-solution-secondary-constructor-complains-if-signature-vararg") {
            assertFailsWith<IllegalArgumentException> {
                Solution.no(querySignature.copy(vararg = true), queryArgList)
            }
        }

        test("halt-solution-contains-inserted-data") {
            val toBeTested = Solution.halt(aQuery, anException)

            assertEquals(aQuery, toBeTested.query)
            assertEquals(anException, toBeTested.exception)
        }

        test("halt-solution-solution-always-null") {
            assertNull(Solution.halt(aQuery, anException).solvedQuery)
        }

        test("halt-solution-substitution-always-failed") {
            assertEquals(Substitution.failed(), Solution.halt(aQuery, anException).substitution)
        }

        test("halt-solution-secondary-constructor-works-as-expected") {
            val toBeTested = Solution.halt(querySignature, queryArgList, anException)

            assertEquals(aQuery, toBeTested.query)
        }

        test("halt-solution-secondary-constructor-complains-if-arity-doesnt-match-arguments-count") {
            assertFailsWith<IllegalArgumentException> { Solution.halt(querySignature, emptyList(), anException) }
            assertFailsWith<IllegalArgumentException> {
                Solution.halt(querySignature, listOf(Truth.TRUE, Truth.TRUE), anException)
            }
        }

        test("halt-solution-secondary-constructor-complains-if-signature-vararg") {
            assertFailsWith<IllegalArgumentException> {
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

            assertEquals(Atom.of("x"), solution.valueOf("X"))
            assertEquals(Atom.of("p"), solution.valueOf("_P"))
        }

        test("clean-up-hides-wildcard-variables-when-asked") {
            val scope = Scope.empty()
            val solution =
                with(scope) {
                    val query = Struct.of("f", varOf("X"), varOf("_P"))
                    val substitution = Substitution.of(mapOf(varOf("X") to Atom.of("x"), varOf("_P") to Atom.of("p")))
                    Solution.yes(query, substitution)
                }.cleanUp(hideWildcardVariables = true)

            assertEquals(Atom.of("x"), solution.valueOf("X"))
            assertNull(solution.valueOf("_P"))
        }

        test("clean-up-hiding-wildcard-variables-is-a-no-op-for-no-and-halt") {
            val no = Solution.no(aQuery).cleanUp(hideWildcardVariables = true)
            val halt = Solution.halt(aQuery, anException).cleanUp(hideWildcardVariables = true)

            assertEquals(Solution.no(aQuery), no)
            assertEquals(Solution.halt(aQuery, anException), halt)
        }

        withTests(nameFn = { "kind/${it.id}/casts-only-to-itself" }, kinds) { case ->
            val solution = case.solution()
            assertEquals(case.id == "yes", solution.asYes() != null)
            assertEquals(case.id == "no", solution.asNo() != null)
            assertEquals(case.id == "halt", solution.asHalt() != null)
            if (case.id != "yes") assertFailsWith<ClassCastException> { solution.castToYes() }
            if (case.id != "no") assertFailsWith<ClassCastException> { solution.castToNo() }
            if (case.id != "halt") assertFailsWith<ClassCastException> { solution.castToHalt() }
        }

        withTests(nameFn = { "kind/${it.id}/when-is-dispatches-to-its-branch" }, kinds) { case ->
            assertEquals(case.id, case.solution().dispatched())
        }

        withTests(nameFn = { "kind/${it.id}/when-is-falls-back-to-otherwise" }, kinds) { case ->
            val solution = case.solution()
            assertEquals("otherwise", solution.whenIs<String>(otherwise = { "otherwise" }))
            assertFailsWith<IllegalStateException> { solution.whenIs<String>() }
        }
    })
