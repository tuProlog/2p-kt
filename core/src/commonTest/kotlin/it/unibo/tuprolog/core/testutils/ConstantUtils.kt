package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Constant
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import kotlin.test.assertSame

/**
 * Utils singleton for testing [Constant]
 */
internal object ConstantUtils {

    /**
     * Asserts that a freshCopy of a ground [Term] is the [Term] itself
     */
    internal fun assertFreshCopyIsItself(constant: Term) {
        assertEqualities(constant, constant.freshCopy())
        assertSame(constant, constant.freshCopy())
    }

    /**
     * Asserts that a freshCopy with Scope of an Constant is the Constant itself
     */
    internal fun assertFreshCopyWithScopeIsItself(constant: Constant) {
        assertEqualities(constant, constant.freshCopy(Scope.empty()))
        assertSame(constant, constant.freshCopy(Scope.empty()))
    }

    /**
     * Assert that apply called (in its variants) on a Constant is the Constant itself
     */
    internal fun assertApplyingSubstitutionIsItself(constant: Constant) {
        assertEqualities(constant, constant.apply(Substitution.empty()))
        assertSame(constant, constant.apply(Substitution.empty()))

        assertEqualities(constant, constant.apply(Substitution.empty(), Substitution.empty()))
        assertSame(constant, constant.apply(Substitution.empty(), Substitution.empty()))

        assertEqualities(constant, constant[Substitution.empty()])
        assertSame(constant, constant[Substitution.empty()])
    }
}
