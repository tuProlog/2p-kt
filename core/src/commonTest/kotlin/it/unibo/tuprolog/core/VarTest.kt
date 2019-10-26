package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.VarImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.VarUtils
import it.unibo.tuprolog.core.testutils.VarUtils.assertDifferentVariableExceptForName
import kotlin.test.*

/**
 * Test class for [Var] companion object
 *
 * @author Enrico
 */
internal class VarTest {

    @Test
    fun varRegexCorrect() {
        assertTrue(VarUtils.correctlyNamedVars.all { it matches Var.VAR_REGEX_PATTERN })
        assertFalse(VarUtils.incorrectlyNamedVars.any { it matches Var.VAR_REGEX_PATTERN })
    }

    @Test
    fun varOfWorksAsExpected() {
        val correctInstances = VarUtils.mixedVars.map { VarImpl(it) }
        val toBeTested = VarUtils.mixedVars.map { Var.of(it) }

        onCorrespondingItems(correctInstances, toBeTested, ::assertDifferentVariableExceptForName)
    }

    @Test
    fun anonymousWorksAsExpected() {
        val anonymousVarInstance = Var.anonymous()
        assertEquals("_", anonymousVarInstance.name)

        assertEqualities(anonymousVarInstance, anonymousVarInstance)

        assertStructurallyEquals(Var.of("_"), anonymousVarInstance)
        assertNotEquals(Var.of("_"), anonymousVarInstance)
        assertNotSame(Var.of("_"), anonymousVarInstance)
    }
}
