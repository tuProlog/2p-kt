package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.VarImpl
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertNotStrictlyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.VarUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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

        onCorrespondingItems(correctInstances, toBeTested) { correct, underTest ->
            assertEquals(correct, underTest)
            assertStructurallyEquals(correct, underTest)
        }
    }

    @Test
    fun anonymousWorksAsExpected() {
        val anonymousVarInstance = Var.anonymous()
        assertEquals("_", anonymousVarInstance.name)

        assertEquals(Var.of("_"), anonymousVarInstance)
        assertStructurallyEquals(Var.of("_"), anonymousVarInstance)
        assertNotStrictlyEquals(Var.of("_"), anonymousVarInstance)
    }
}
