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
    /** A named [Fact] head, for parameterized tests */
    internal data class FactCase(
        val id: String,
        val head: Struct,
    )

    /** Contains ground Facts (aka without variables) */
    val groundFactCases by lazy {
        listOf(
            FactCase("parent-of-atoms", Struct.of("parent", Atom.of("jack"), Atom.of("bob"))),
            FactCase("parent-of-numbers", Struct.of("parent", Integer.of(5), Real.of("2.5"))),
            FactCase("true", Truth.TRUE),
        )
    }

    /** Contains non ground Facts, with variables */
    val nonGroundFactCases by lazy {
        listOf(
            FactCase("anonymous-variables", Struct.of("myFunc", Var.anonymous(), Var.anonymous())),
            FactCase("variable", Struct.of("win", Var.of("X"))),
        )
    }

    /** Contains mixed [groundFactCases] and [nonGroundFactCases] */
    val mixedFactCases by lazy { groundFactCases + nonGroundFactCases }
}
