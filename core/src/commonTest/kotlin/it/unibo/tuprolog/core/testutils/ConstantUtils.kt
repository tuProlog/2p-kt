package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Term
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Utils singleton for testing [Constant] TODO SEE Issue #1 on gitlab!! Constant interface to be added
 */
internal object ConstantUtils {

    /**
     * Asserts that a freshCopy of an Constant is the Constant itself
     */
    internal fun assertFreshCopyIsItself(constant: Term) { // TODO replace Term interface with constant interface when it will be available
        assertEquals(constant, constant.freshCopy())
        assertTrue(constant structurallyEquals constant.freshCopy())
        assertTrue(constant strictlyEquals constant.freshCopy())
        assertSame(constant, constant.freshCopy())
    }
}
