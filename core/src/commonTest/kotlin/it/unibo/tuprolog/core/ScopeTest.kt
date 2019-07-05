package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.ScopeImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.ScopeUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 * Test class for [Scope] companion object
 *
 * @author Enrico
 */
internal class ScopeTest {

    private val correctEmptyInstance = ScopeImpl(ScopeUtils.emptyScope)
    private val correctNonEmptyScopeInstances = ScopeUtils.nonEmptyScopes.map(::ScopeImpl)

    @Test
    fun emptyReturnsEmptyScope() {
        val toBeTested = Scope.empty()

        assertEquals(correctEmptyInstance, toBeTested)
    }

    @Test
    fun scopeOfVarargVariablesWorksAsExpected() {
        val toBeTested = ScopeUtils.nonEmptyScopeVars.map { Scope.of(*it.toTypedArray()) }

        onCorrespondingItems(correctNonEmptyScopeInstances, toBeTested) { expected, actual ->
            assertEquals(expected, actual)
        }
    }

    @Test
    fun scopeOfVarargStringsWorksAsExpected() {
        val toBeTested = ScopeUtils.nonEmptyScopeVarNames.map { Scope.of(*it.toTypedArray()) }

        onCorrespondingItems(correctNonEmptyScopeInstances, toBeTested) { expected, actual ->
            assertEquals(expected, actual)
        }
    }

    @Test
    fun scopeOfVarargsExecutesLambda() {
        assertFailsWith<IllegalStateException> { Scope.of(Var.of("A")) { throw IllegalStateException() } }
        assertFailsWith<IllegalStateException> { Scope.of("A") { throw IllegalStateException() } }
    }
}
