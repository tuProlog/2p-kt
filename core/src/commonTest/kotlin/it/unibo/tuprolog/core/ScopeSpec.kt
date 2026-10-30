@file:Suppress("RemoveRedundantSpreadOperator")

package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.impl.ScopeImpl
import it.unibo.tuprolog.core.testutils.ScopeUtils
import it.unibo.tuprolog.core.testutils.ScopeUtils.assertScopeCorrectContents
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

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

            assertEquals(correctEmptyInstance, toBeTested)
        }

        test("scope-of-no-vararg-returns-empty-scope") {
            assertEquals(Scope.empty(), Scope.of())
            assertEquals(Scope.empty(), Scope.empty { this })
            assertEquals(Scope.empty(), Scope.of { this })
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
            assertFailsWith<IllegalStateException> { Scope.of(Var.of("A")) { throw IllegalStateException() } }
            assertFailsWith<IllegalStateException> { Scope.of("A") { throw IllegalStateException() } }
            assertFailsWith<IllegalStateException> { Scope.of<Nothing>(Var.of("A")) { throw IllegalStateException() } }
            assertFailsWith<IllegalStateException> { Scope.of<Nothing>("A") { throw IllegalStateException() } }
        }

        test("empty-with-lambda-creates-correct-instance-and-returns-lambda-result") {
            val correctResult = 3
            val toBeTestedResult =
                Scope.empty {
                    assertEquals(correctEmptyInstance, this)
                    correctResult
                }

            assertEquals(correctResult, toBeTestedResult)
        }

        test("scope-of-with-lambda-creates-correct-instance-and-returns-lambda-result") {
            val myResult = 3
            Var.of("A").let { aVar ->
                val correct = Scope.of(aVar)
                val toBeTestedResult =
                    Scope.of<Int>(aVar) {
                        assertEquals(correct, this)
                        myResult
                    }

                assertEquals(myResult, toBeTestedResult)

                val toBeTestedResult2 =
                    Scope.of<Int>(aVar.name) {
                        assertEquals(aVar.name, this[aVar.name]?.name)
                        myResult
                    }
                assertEquals(myResult, toBeTestedResult2)
            }
        }
    })
