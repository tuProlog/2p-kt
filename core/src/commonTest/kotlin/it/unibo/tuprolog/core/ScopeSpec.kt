@file:Suppress("RemoveRedundantSpreadOperator")

package it.unibo.tuprolog.core

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.impl.ScopeImpl
import it.unibo.tuprolog.core.testutils.ScopeUtils
import it.unibo.tuprolog.core.testutils.ScopeUtils.assertScopeCorrectContents

/**
 * Test class for [Scope] companion object
 *
 * @author Enrico
 */
class ScopeSpec :
    FunSpec({
        val correctEmptyInstance = ScopeImpl(ScopeUtils.emptyScope)

        test("empty-returns-empty-scope") {
            val toBeTested = Scope.empty()

            toBeTested shouldBe correctEmptyInstance
        }

        test("scope-of-no-vararg-returns-empty-scope") {
            Scope.of() shouldBe Scope.empty()
            (Scope.empty { this }) shouldBe Scope.empty()
            (Scope.of { this }) shouldBe Scope.empty()
        }

        withTests(nameFn = { "of-variables/${it.id}" }, ScopeUtils.nonEmptyScopeCases) { case ->
            assertScopeCorrectContents(
                ScopeImpl(case.scope()).variables,
                Scope.of(case.scope().values.toList()).variables,
            )
        }

        withTests(nameFn = { "of-vararg-strings/${it.id}" }, ScopeUtils.nonEmptyScopeCases) { case ->
            assertScopeCorrectContents(
                ScopeImpl(case.scope()).variables,
                Scope.of(*case.varNames.toTypedArray()).variables,
            )
        }

        test("scope-of-varargs-executes-lambda") {
            shouldThrow<IllegalStateException> { Scope.of(Var.of("A")) { throw IllegalStateException() } }
            shouldThrow<IllegalStateException> { Scope.of("A") { throw IllegalStateException() } }
            shouldThrow<IllegalStateException> { Scope.of<Nothing>(Var.of("A")) { throw IllegalStateException() } }
            shouldThrow<IllegalStateException> { Scope.of<Nothing>("A") { throw IllegalStateException() } }
        }

        test("empty-with-lambda-creates-correct-instance-and-returns-lambda-result") {
            val correctResult = 3
            val toBeTestedResult =
                Scope.empty {
                    this shouldBe correctEmptyInstance
                    correctResult
                }

            toBeTestedResult shouldBe correctResult
        }

        test("scope-of-with-lambda-creates-correct-instance-and-returns-lambda-result") {
            val myResult = 3
            Var.of("A").let { aVar ->
                val correct = Scope.of(aVar)
                val toBeTestedResult =
                    Scope.of<Int>(aVar) {
                        this shouldBe correct
                        myResult
                    }

                toBeTestedResult shouldBe myResult

                val toBeTestedResult2 =
                    Scope.of<Int>(aVar.name) {
                        this[aVar.name]?.name shouldBe aVar.name
                        myResult
                    }
                toBeTestedResult2 shouldBe myResult
            }
        }

        test("of-sequence/holds-the-variables") {
            val x = Var.of("X")
            Scope.of(sequenceOf(x))["X"] shouldBe x
        }

        test("of-variabled/holds-its-variables") {
            val x = Var.of("X")
            Scope.of(Struct.of("f", x))["X"] shouldBe x
        }

        test("of-with-lambda/runs-in-the-scope") {
            val x = Var.of("X")
            val f = Struct.of("f", x)
            (Scope.of(x) { this["X"] }) shouldBe x
            (Scope.of(listOf(x)) { this["X"] }) shouldBe x
            (Scope.of(sequenceOf(x)) { this["X"] }) shouldBe x
            (Scope.of(f) { this["X"] }) shouldBe x
        }

        test("logic-list-from/terms-are-the-last-the-tail") {
            val (a, b) = listOf(Atom.of("a"), Atom.of("b"))
            Scope.empty {
                logicListFrom(a, b) shouldBe List.from(listOf(a, b), null)
                logicListFrom(listOf(a, b)) shouldBe List.from(listOf(a, b), null)
            }
        }

        test("equal-scopes-have-equal-hashes") {
            val x = Var.of("X")
            Scope.of(x).hashCode() shouldBe Scope.of(x).hashCode()
            Scope.of(x).toString() shouldBe "{X=$x}"
        }
    })
