package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import kotlin.test.assertSame

/**
 * Utils singleton for testing [Constant] TODO SEE Issue #1 on gitlab!! Constant interface to be added
 */
internal object ConstantUtils {

    /**
     * Asserts that a freshCopy of an Constant is the Constant itself
     */
    internal fun assertFreshCopyIsItself(constant: Term) { // TODO replace Term interface with Constant interface when it will be available
        AssertionUtils.assertEqualities(constant, constant.freshCopy())
        assertSame(constant, constant.freshCopy())
    }

    /**
     * Asserts that a freshCopy with Scope of an Constant is the Constant itself
     */
    internal fun assertFreshCopyWithScopeIsItself(constant: Term) { // TODO replace Term interface with Constant interface when it will be available
        AssertionUtils.assertEqualities(constant, constant.freshCopy(Scope.empty()))
        assertSame(constant, constant.freshCopy(Scope.empty()))
    }
}
