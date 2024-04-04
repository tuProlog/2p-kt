package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.ScopeImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.ScopeUtils
import it.unibo.tuprolog.core.testutils.ScopeUtils.assertScopeCorrectContents
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotSame
import kotlin.test.assertSame

/**
 * Test class for [Scope] companion object
 *
 * @author Enrico
 */
class ScopeTest {
    private val correctEmptyInstance = ScopeImpl(ScopeUtils.emptyScope)
    private val correctNonEmptyScopeInstances = ScopeUtils.nonEmptyScopes.map(::ScopeImpl)

    @Test
    fun emptyReturnsEmptyScope() {
        val toBeTested = Scope.empty()

        assertEquals(correctEmptyInstance, toBeTested)
    }

    @Test
    @Suppress("RemoveRedundantSpreadOperator")
    fun scopeOfNoVarargReturnsEmptyScope() {
        assertEquals(Scope.empty(), Scope.of())
        assertEquals(Scope.empty(), Scope.empty { this })
        assertEquals(Scope.empty(), Scope.of { this })
    }

    @Test
    fun scopeOfVarargVariablesWorksAsExpected() {
        val toBeTested = ScopeUtils.nonEmptyScopeVars.map { Scope.of(it) }.map { it.variables }

        onCorrespondingItems(
            correctNonEmptyScopeInstances.map { it.variables },
            toBeTested,
            ::assertScopeCorrectContents,
        )
    }

    @Test
    fun scopeOfVarargStringsWorksAsExpected() {
        val toBeTested = ScopeUtils.nonEmptyScopeVarNames.map { Scope.of(*it.toTypedArray()) }.map { it.variables }

        onCorrespondingItems(
            correctNonEmptyScopeInstances.map { it.variables },
            toBeTested,
            ::assertScopeCorrectContents,
        )
    }

    @Test
    fun scopeOfVarargsExecutesLambda() {
        assertFailsWith<IllegalStateException> { Scope.of(Var.of("A")) { throw IllegalStateException() } }
        assertFailsWith<IllegalStateException> { Scope.of("A") { throw IllegalStateException() } }
        assertFailsWith<IllegalStateException> { Scope.of<Nothing>(Var.of("A")) { throw IllegalStateException() } }
        assertFailsWith<IllegalStateException> { Scope.of<Nothing>("A") { throw IllegalStateException() } }
    }

    @Test
    fun emptyWithLambdaCreatesCorrectInstanceAndReturnsLambdaResult() {
        val correctResult = 3
        val toBeTestedResult =
            Scope.empty {
                assertEquals(correctEmptyInstance, this)
                correctResult
            }

        assertEquals(correctResult, toBeTestedResult)
    }

    @Test
    fun scopeOfWithLambdaCreatesCorrectInstanceAndReturnsLambdaResult() {
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

    private fun scopeCreatesSameNamedVariables(
        f: Scope.(String) -> Var,
        name: String,
    ) {
        val scope = Scope.empty()
        val var1 = scope.f(name)
        val var2 = scope.f(name)
        assertSame(var1, var2)
    }

    @Test
    fun scopeCreatesSameNamedVariables() {
        scopeCreatesSameNamedVariables(Scope::varOf, "A")
    }

    @Test
    fun scopeCreatesSameUnderscoredVariables() {
        scopeCreatesSameNamedVariables(Scope::varOf, "_")
    }

    private fun scopeCreatesDifferentAnonymousVariables(f: Scope.() -> Var) {
        val scope = Scope.empty()
        val var1 = scope.f()
        val var2 = scope.f()
        assertNotSame(var1, var2)
    }

    @Test
    fun scopeCreatesDifferentAnonymousVariables1() {
        scopeCreatesDifferentAnonymousVariables(Scope::anonymous)
    }

    @Test
    fun scopeCreatesDifferentAnonymousVariables2() {
        scopeCreatesDifferentAnonymousVariables(Scope::anonymousVar)
    }

    @Test
    fun scopeCreatesDifferentAnonymousVariables3() {
        scopeCreatesDifferentAnonymousVariables(Scope::whatever)
    }

    @Test
    fun scopeCreatesDifferentAnonymousVariables4() {
        scopeCreatesDifferentAnonymousVariables(Scope::`_`)
    }
}
