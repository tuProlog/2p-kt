package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Block
import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var

/**
 * Utils singleton for testing [Block]
 *
 * @author Enrico
 */
internal object BlockUtils {
    /** Contains block arguments, containing ground terms */
    internal val groundBlocks by lazy {
        listOf(
            arrayOf(Truth.TRUE),
            arrayOf(Atom.of("hey"), Atom.of("!")),
            arrayOf(Integer.of(1), Real.of(1.5), Truth.FALSE, Atom.of("ciao")),
        )
    }

    /** Contains [groundBlocks] wrapped with Tuple if needed to be used in constructing Block instances */
    internal val groundBlocksTupleWrapped by lazy { groundBlocks.map { Tuple.wrapIfNeeded(*it) } }

    /** Contains block arguments, containing non ground terms (aka variables or containing variables) */
    internal val nonGroundBlocks by lazy {
        listOf(
            arrayOf(Var.of("MyVar")),
            arrayOf(Var.of("A"), Var.anonymous(), Cons.singleton(Var.of("Var"))),
        )
    }

    /** Contains [nonGroundBlocks] wrapped with Tuple if needed to be used in constructing Block instances */
    internal val nonGroundBlocksTupleWrapped by lazy { nonGroundBlocks.map { Tuple.wrapIfNeeded(*it) } }

    /** Contains [groundBlocks] and [nonGroundBlocks] mixed */
    internal val mixedBlocks by lazy { groundBlocks + nonGroundBlocks }

    /** Contains [mixedBlocks] wrapped with Tuple if needed to be used in constructing Block instances */
    internal val mixedBlocksTupleWrapped by lazy { mixedBlocks.map { Tuple.wrapIfNeeded(*it) } }
}
