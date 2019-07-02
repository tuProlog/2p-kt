package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import kotlin.test.assertEquals
import kotlin.test.assertNotSame

/**
 * Utils singleton for testing [Struct]
 *
 * @author Enrico
 */
internal object StructUtils {

    /**
     * Asserts that passed term with variables, when copied, renews variables contained;
     *
     * This means that copied Term will be structurallyEquals to the original, but not the same and not strictlyEquals
     */
    internal fun assertFreshCopyRenewsContainedVariables(withVariables: Term) {
        val copy = withVariables.freshCopy()

        assertEquals(withVariables, copy)
        AssertionUtils.assertStructurallyEquals(withVariables, copy)
        AssertionUtils.assertNotStrictlyEquals(withVariables, copy)
        assertNotSame(withVariables, copy)
    }
}
