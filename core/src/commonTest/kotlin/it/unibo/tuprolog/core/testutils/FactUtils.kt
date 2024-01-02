package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var

/**
 * Utils singleton for testing [Fact]
 *
 * @author Enrico
 */
internal object FactUtils {
    /** Contains ground Facts (aka without variables) */
    val groundFacts by lazy {
        listOf(
            Struct.of("parent", Atom.of("jack"), Atom.of("bob")),
            Struct.of("parent", Integer.of(5), Real.of("2.5")),
            Truth.TRUE,
        )
    }

    /** Contains non ground Facts, with variables */
    val nonGroundFacts by lazy {
        listOf(
            Struct.of("myFunc", Var.anonymous(), Var.anonymous()),
            Struct.of("win", Var.of("X")),
        )
    }

    /** Contains mixed [groundFacts] and [nonGroundFacts] */
    val mixedFacts by lazy { groundFacts + nonGroundFacts }
}
