package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Block
import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var

/**
 * Utils singleton for testing [Block]
 *
 * @author Enrico
 */
internal object BlockUtils {
    /** Named block arguments, for parameterized tests */
    internal data class BlockCase(
        val id: String,
        val elements: List<Term>,
    ) {
        /** The [elements] wrapped with Tuple if needed to be used in constructing Block instances */
        val tupleWrapped: Term get() = Tuple.wrapIfNeeded(*elements.toTypedArray())
    }

    /** Contains block arguments, containing ground terms */
    internal val groundBlockCases by lazy {
        listOf(
            BlockCase("true", listOf(Truth.TRUE)),
            BlockCase("two-atoms", listOf(Atom.of("hey"), Atom.of("!"))),
            BlockCase("four-constants", listOf(Integer.of(1), Real.of(1.5), Truth.FALSE, Atom.of("ciao"))),
        )
    }

    /** Contains block arguments, containing non ground terms (aka variables or containing variables) */
    internal val nonGroundBlockCases by lazy {
        listOf(
            BlockCase("one-variable", listOf(Var.of("MyVar"))),
            BlockCase("variables-and-cons", listOf(Var.of("A"), Var.anonymous(), Cons.singleton(Var.of("Var")))),
        )
    }

    /** Contains [groundBlockCases] and [nonGroundBlockCases] mixed */
    internal val mixedBlockCases by lazy { groundBlockCases + nonGroundBlockCases }
}
