package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.*

/**
 * Utils singleton for testing [Set]
 *
 * @author Enrico
 */
internal object SetUtils {

    /**
     * Contains set arguments, containing ground terms
     */
    internal val groundSets by lazy {
        listOf(
                arrayOf(Truth.`true`()),
                arrayOf(Atom.of("hey"), Atom.of("!")),
                arrayOf(Integer.of(1), Real.of(1.5), Truth.fail(), Atom.of("ciao"))
        )
    }
    /**
     * Contains [groundSets] wrapped with Tuple if needed to be used in constructing Set instances
     */
    internal val groundSetsTupleWrapped by lazy { groundSets.map { Tuple.wrapIfNeeded(*it) } }

    /**
     * Contains set arguments, containing non ground terms (aka variables or containing variables)
     */
    internal val notGroundSets by lazy {
        listOf(
                arrayOf(Var.of("MyVar")),
                arrayOf(Var.of("A"), Var.anonymous(), Cons.last(Var.of("Var")))
        )
    }

    /**
     * Contains [notGroundSets] wrapped with Tuple if needed to be used in constructing Set instances
     */
    internal val notGroundSetsTupleWrapped by lazy { notGroundSets.map { Tuple.wrapIfNeeded(*it) } }

    /**
     * Contains [groundSets] and [notGroundSets] mixed
     */
    internal val mixedSets by lazy { groundSets + notGroundSets }

    /**
     * Contains [mixedSets] wrapped with Tuple if needed to be used in constructing Set instances
     */
    internal val mixedSetsTupleWrapped by lazy { mixedSets.map { Tuple.wrapIfNeeded(*it) } }
}
