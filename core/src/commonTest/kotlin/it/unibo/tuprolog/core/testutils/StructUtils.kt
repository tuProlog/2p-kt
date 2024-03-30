package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Block
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Empty
import it.unibo.tuprolog.core.EmptyBlock
import it.unibo.tuprolog.core.EmptyList
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import kotlin.test.assertNotEquals
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
     * This means that copied Term will be structurallyEquals to the original, but not equals, not the same and not strictlyEquals
     */
    internal fun <T : Term> assertFreshCopyRenewsContainedVariables(withVariables: T) {
        val copy = withVariables.freshCopy()

        assertStructurallyEquals(withVariables, copy)
        assertNotEquals(withVariables, copy)
        assertNotSame(withVariables, copy)
    }

    /**
     * Asserts that doing a freshCopy over created term (created trough passed [constructor]) with two different Variables instances
     * that share the same name, after the copy those variables will be tied to each other
     */
    internal fun <T : Struct> assertFreshCopyMergesDifferentVariablesWithSameName(constructor: (Var, Var) -> T) {
        val termWithSameVarName = constructor(Var.of("A"), Var.of("A"))

        val firstVarBefore = termWithSameVarName.args[0]
        val secondVarBefore = termWithSameVarName.args[1]

        assertStructurallyEquals(firstVarBefore, secondVarBefore)
        assertNotEquals(firstVarBefore, secondVarBefore)
        assertNotSame(firstVarBefore, secondVarBefore)

        val consCopied = termWithSameVarName.freshCopy()

        val firstVarAfter = consCopied.args[0]
        val secondVarAfter = consCopied.args[1]

        AssertionUtils.assertEqualities(firstVarAfter, secondVarAfter)
        assertSame(firstVarAfter, secondVarAfter)
    }

    /** For non special Structs are intended all valid Structs that has not a corresponding representation in any Struct subclass */
    internal val nonSpecialStructs by lazy {
        listOf(
            "ciao" to listOf<Term>(Truth.TRUE),
            "myFunctor" to listOf<Term>(Atom.of("hello"), Atom.of("world"), Atom.of("!")),
            "varFunctor" to listOf<Term>(Var.of("A"), Var.of("B")),
            "{}" to listOf<Term>(Integer.of(1), Integer.of(2), Integer.of(3)),
            "[]" to listOf<Term>(Integer.of(1), Integer.of(2), Integer.of(3)),
            "." to listOf(Real.of(0.1), Real.of(0.5f), Var.of("MyVar")),
            "," to listOf<Term>(EmptyList(), EmptyBlock(), Truth.TRUE),
            "/" to listOf<Term>(Truth.FALSE),
            "sameVar" to Scope.empty { listOf<Term>(varOf("X"), varOf("X")) },
        )
    }

    /** Special Structs are those Structs that can be mapped exactly in a subclass of Struct */
    internal val specialStructs by lazy {
        listOf(
            Cons.FUNCTOR to listOf<Term>(Var.of("H"), Var.of("T")),
            Block.FUNCTOR to listOf(Tuple.wrapIfNeeded(Atom.of("My atom"))),
            Tuple.FUNCTOR to listOf<Term>(Atom.of("left"), Atom.of("right")),
            Clause.FUNCTOR to listOf<Term>(Atom.of("rule1"), Atom.of("rule2")),
            Clause.FUNCTOR to listOf<Term>(Atom.of("myDirective")),
            Clause.FUNCTOR to listOf<Term>(Atom.of("myFact"), Truth.TRUE),
            "myAtom" to listOf(),
            Empty.EMPTY_LIST_FUNCTOR to listOf(),
            Empty.EMPTY_BLOCK_FUNCTOR to listOf(),
            Truth.TRUE_FUNCTOR to listOf(),
            Truth.FAIL_FUNCTOR to listOf(),
            Indicator.FUNCTOR to listOf<Term>(Atom.of("func"), Integer.of(4)),
        )
    }

    /** All Structs under testing, [specialStructs] and [nonSpecialStructs] */
    internal val mixedStructs by lazy { nonSpecialStructs + specialStructs }

    /** All under testing ground Structs, those containing at least one variable */
    internal val groundStructs by lazy { mixedStructs.filterNot { (_, args) -> args.any { it.isVar } } }

    /** All not ground Structs, those not containing variables */
    internal val nonGroundStructs by lazy { mixedStructs - groundStructs }

    /** All Struct functors */
    internal val mixedStructFunctors by lazy { mixedStructs.map { (functor, _) -> functor } }

    /** All Struct arguments */
    internal val mixedStructArguments by lazy { mixedStructs.map { (_, args) -> args } }

    /** All Struct variables */
    internal val mixedStructVariables by lazy { mixedStructArguments.map { it.filter(Term::isVar) } }
}
