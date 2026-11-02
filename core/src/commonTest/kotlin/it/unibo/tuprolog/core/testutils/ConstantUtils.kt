package it.unibo.tuprolog.core.testutils

import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.Constant
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities

/**
 * Utils singleton for testing [Constant]
 *
 * @author Enrico
 */
internal object ConstantUtils {
    /** Asserts that a freshCopy of a ground [Term] is the [Term] itself */
    internal fun assertFreshCopyIsItself(constant: Term) {
        assertEqualities(constant, constant.freshCopy())
        constant.freshCopy() shouldBeSameInstanceAs constant
    }

    /** Asserts that a freshCopy with Scope of an Constant is the Constant itself */
    internal fun assertFreshCopyWithScopeIsItself(constant: Constant) {
        assertEqualities(constant, constant.freshCopy(Scope.empty()))
        constant.freshCopy(Scope.empty()) shouldBeSameInstanceAs constant
    }

    /** Assert that apply called (in its variants) on a Constant is the Constant itself */
    internal fun assertApplyingSubstitutionIsItself(constant: Constant) {
        assertEqualities(constant, constant.apply(Substitution.empty()))
        constant.apply(Substitution.empty()) shouldBeSameInstanceAs constant

        assertEqualities(constant, constant.apply(Substitution.empty(), Substitution.empty()))
        constant.apply(Substitution.empty(), Substitution.empty()) shouldBeSameInstanceAs constant

        assertEqualities(constant, constant[Substitution.empty()])
        constant[Substitution.empty()] shouldBeSameInstanceAs constant
    }
}
