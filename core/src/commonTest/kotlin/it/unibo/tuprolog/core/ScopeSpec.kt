@file:Suppress("RemoveRedundantSpreadOperator")

package it.unibo.tuprolog.core

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.core.impl.ScopeImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
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

        val correctNonEmptyScopeInstances = ScopeUtils.nonEmptyScopes.map(::ScopeImpl)

        test("empty-returns-empty-scope") {
            val toBeTested = Scope.empty()

            assertEquals(correctEmptyInstance, toBeTested)
        }

        test("scope-of-no-vararg-returns-empty-scope") {
            assertEquals(Scope.empty(), Scope.of())
            assertEquals(Scope.empty(), Scope.empty { this })
            assertEquals(Scope.empty(), Scope.of { this })
        }

        test("scope-of-vararg-variables-works-as-expected") {
            val toBeTested = ScopeUtils.nonEmptyScopeVars.map { Scope.of(it) }.map { it.variables }

            onCorrespondingItems(
                correctNonEmptyScopeInstances.map { it.variables },
                toBeTested,
                ::assertScopeCorrectContents,
            )
        }

        test("scope-of-vararg-strings-works-as-expected") {
            val toBeTested = ScopeUtils.nonEmptyScopeVarNames.map { Scope.of(*it.toTypedArray()) }.map { it.variables }

            onCorrespondingItems(
                correctNonEmptyScopeInstances.map { it.variables },
                toBeTested,
                ::assertScopeCorrectContents,
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
