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

    /** A named [Struct] functor and arguments pair, for parameterized tests */
    internal data class StructCase(
        val id: String,
        val functor: String,
        val args: List<Term>,
    ) {
        /** Whether no argument is a [Var] */
        val isGround: Boolean get() = args.none { it.isVar }
    }

    /** For non special Structs are intended all valid Structs that has not a corresponding representation in any Struct subclass */
    internal val nonSpecialStructCases by lazy {
        listOf(
            StructCase("ciao-1", "ciao", listOf(Truth.TRUE)),
            StructCase("my-functor-3", "myFunctor", listOf(Atom.of("hello"), Atom.of("world"), Atom.of("!"))),
            StructCase("var-functor-2", "varFunctor", listOf(Var.of("A"), Var.of("B"))),
            StructCase("curly-braces-3", "{}", listOf(Integer.of(1), Integer.of(2), Integer.of(3))),
            StructCase("square-brackets-3", "[]", listOf(Integer.of(1), Integer.of(2), Integer.of(3))),
            StructCase("dot-3", ".", listOf(Real.of(0.1), Real.of(0.5f), Var.of("MyVar"))),
            StructCase("comma-3", ",", listOf(EmptyList(), EmptyBlock(), Truth.TRUE)),
            StructCase("slash-1", "/", listOf(Truth.FALSE)),
            StructCase("same-var-2", "sameVar", Scope.empty { listOf(varOf("X"), varOf("X")) }),
        )
    }

    /** Special Structs are those Structs that can be mapped exactly in a subclass of Struct */
    internal val specialStructCases by lazy {
        listOf(
            StructCase("cons", Cons.FUNCTOR, listOf(Var.of("H"), Var.of("T"))),
            StructCase("block", Block.FUNCTOR, listOf(Tuple.wrapIfNeeded(Atom.of("My atom")))),
            StructCase("tuple", Tuple.FUNCTOR, listOf(Atom.of("left"), Atom.of("right"))),
            StructCase("rule", Clause.FUNCTOR, listOf(Atom.of("rule1"), Atom.of("rule2"))),
            StructCase("directive", Clause.FUNCTOR, listOf(Atom.of("myDirective"))),
            StructCase("fact", Clause.FUNCTOR, listOf(Atom.of("myFact"), Truth.TRUE)),
            StructCase("atom", "myAtom", listOf()),
            StructCase("empty-list", Empty.EMPTY_LIST_FUNCTOR, listOf()),
            StructCase("empty-block", Empty.EMPTY_BLOCK_FUNCTOR, listOf()),
            StructCase("true", Truth.TRUE_FUNCTOR, listOf()),
            StructCase("fail", Truth.FAIL_FUNCTOR, listOf()),
            StructCase("indicator", Indicator.FUNCTOR, listOf(Atom.of("func"), Integer.of(4))),
        )
    }

    /** All Structs under testing, [specialStructCases] and [nonSpecialStructCases] */
    internal val mixedStructCases by lazy { nonSpecialStructCases + specialStructCases }
}
