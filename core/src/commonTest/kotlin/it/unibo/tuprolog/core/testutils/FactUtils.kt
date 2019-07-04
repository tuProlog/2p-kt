package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.*

/**
 * Utils singleton for testing [Fact]
 *
 * @author Enrico
 */
internal object FactUtils {

    /**
     * Contains ground Facts (aka without variables)
     */
    val groundFacts by lazy {
        listOf(
                Struct.of("parent", Atom.of("jack"), Atom.of("bob")),
                Truth.`true`()
        )
    }
    /**
     * Contains non ground Facts, with variables
     */
    val nonGroundFacts by lazy {
        listOf(
                Struct.of("myFunc", Var.anonymous(), Var.anonymous()),
                Struct.of("win", Var.of("X"))
        )
    }

    /**
     * Contains mixed [groundFacts] and [nonGroundFacts]
     */
    val mixedFacts by lazy { groundFacts + nonGroundFacts }

}
