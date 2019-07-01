package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.*

/**
 * Utils singleton for testing [Set]
 *
 * @author Enrico
 */
internal object SetUtils {

    /**
     * Contains sets containing ground terms
     */
    internal val groundSets by lazy {
        listOf(
                arrayOf(Integral.of(1), Real.of(1.5), Truth.fail(), Atom.of("ciao"))
        )
    }

    /**
     * Contains sets containing non ground terms (aka variables or containing variables)
     */
    internal val notGroundSets by lazy {
        listOf(
                arrayOf(Var.of("A"), Var.anonymous(), Couple.last(Var.of("Var")))
        )
    }

    /**
     * Contains ground and notGround sets
     */
    internal val mixedSets by lazy { groundSets + notGroundSets }
}
