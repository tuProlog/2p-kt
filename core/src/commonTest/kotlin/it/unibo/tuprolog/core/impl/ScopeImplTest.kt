package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.ScopeUtils
import kotlin.test.*

/**
 * Test class for [ScopeImpl] and [Scope]
 *
 * @author Enrico
 */
internal class ScopeImplTest {

    private lateinit var emptyScopeInstance: Scope
    private lateinit var nonEmptyScopeInstances: List<Scope>
    private lateinit var mixedScopeInstances: List<Scope>

    @BeforeTest
    fun init() {
        emptyScopeInstance = ScopeImpl(ScopeUtils.emptyScope)
        nonEmptyScopeInstances = ScopeUtils.nonEmptyScopes.map(::ScopeImpl)
        mixedScopeInstances = ScopeUtils.mixedScopes.map(::ScopeImpl)
    }

    @Test
    fun containsVarWorksAsExpected() {
        assertFalse { emptyScopeInstance.contains(Var.of("A")) }

        onCorrespondingItems(ScopeUtils.nonEmptyScopeVars, nonEmptyScopeInstances) { containedVars, scope ->
            containedVars.forEach { assertTrue { it in scope } }
        }
    }

    @Test
    fun containsStringWorksAsExpected() {
        assertFalse { emptyScopeInstance.contains("A") }

        onCorrespondingItems(ScopeUtils.nonEmptyScopeVarNames, nonEmptyScopeInstances) { containedVars, scope ->
            containedVars.forEach { assertTrue { it in scope } }
        }
    }

    @Test
    fun variablesCorrect() {
        onCorrespondingItems(ScopeUtils.mixedScopes, mixedScopeInstances.map { it.variables }) { expected, actual ->
            assertEquals(expected, actual)
        }
    }

    @Test
    fun varOfStringWithNonPresentVariableNameShouldInsertANewOneAmongOthers() {
        emptyScopeInstance.varOf("Test")
        assertEquals(1, emptyScopeInstance.variables.count())


        val scopeVarInitialCounts = ScopeUtils.nonEmptyScopeVarNames.map { it.count() }
        val newVarNames = ScopeUtils.nonEmptyScopeVarNames.map { scopeVarNames -> scopeVarNames.map { it + "x" } }
        onCorrespondingItems(newVarNames, nonEmptyScopeInstances) { toBeAddedVars, nonEmptyScope ->
            toBeAddedVars.forEach { nonEmptyScope.varOf(it) }
        }

        onCorrespondingItems(scopeVarInitialCounts, nonEmptyScopeInstances) { initialCount, scopeInstance ->
            assertEquals(initialCount * 2, scopeInstance.variables.count())
        }
    }

    @Test
    fun varOfStringWithAlreadyContainedVariableShouldNotInsertThatVariableAgain() {
        val scopeVarInitialCounts = ScopeUtils.nonEmptyScopeVarNames.map { it.count() }
        onCorrespondingItems(ScopeUtils.nonEmptyScopeVarNames, nonEmptyScopeInstances) { presentVars, nonEmptyScope ->
            presentVars.forEach { nonEmptyScope.varOf(it) }
        }

        onCorrespondingItems(scopeVarInitialCounts, nonEmptyScopeInstances) { initialCount, scopeInstance ->
            assertEquals(initialCount, scopeInstance.variables.count())
        }
    }

    @Test
    fun whereExecutesTheGivenLambda() {
        mixedScopeInstances.forEach { aScope ->
            assertFailsWith<IllegalStateException> { aScope.where { throw IllegalStateException() } }
        }
    }
}