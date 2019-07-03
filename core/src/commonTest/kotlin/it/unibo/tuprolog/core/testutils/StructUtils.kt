package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

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
    internal fun <T : Term> assertFreshCopyRenewsContainedVariables(withVariables: T) {
        val copy = withVariables.freshCopy()

        assertEquals(withVariables, copy)
        AssertionUtils.assertStructurallyEquals(withVariables, copy)
        AssertionUtils.assertNotStrictlyEquals(withVariables, copy)
        assertNotSame(withVariables, copy)
    }

    /**
     * Asserts that doing a freshCopy over created term (created trough passed [constructor]) with two different Variables instances
     * that share the same name, after the copy those variables will be tied to each other
     */
    internal fun <T : Struct> assertFreshCopyMergesDifferentVariablesWithSameName(constructor: (Term, Term) -> T) {
        val termWithSameVarName = constructor(Var.of("A"), Var.of("A"))

        val firstVarBefore = termWithSameVarName.args[0]
        val secondVarBefore = termWithSameVarName.args[1]

        AssertionUtils.assertStructurallyEquals(firstVarBefore, secondVarBefore)
        AssertionUtils.assertNotStrictlyEquals(firstVarBefore, secondVarBefore)
        assertNotSame(firstVarBefore, secondVarBefore)

        val coupleCopied = termWithSameVarName.freshCopy()

        val firstVarAfter = coupleCopied.args[0]
        val secondVarAfter = coupleCopied.args[1]

        AssertionUtils.assertEqualities(firstVarAfter, secondVarAfter)
        assertSame(firstVarAfter, secondVarAfter)
    }
}
