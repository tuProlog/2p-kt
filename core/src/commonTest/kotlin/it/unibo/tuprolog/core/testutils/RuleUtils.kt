package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.*

/**
 * Utils singleton for testing [Rule]
 *
 * @author Enrico
 */
internal object RuleUtils {

    /**
     * Contains ground Rules (aka without variables)
     */
    val groundRules by lazy {
        listOf(
                Struct.of("parent", Atom.of("bob"), Atom.of("jack"))
                        to Struct.of("son", Atom.of("jack"), Atom.of("bob")),

                Struct.of("money", Integral.of(100)) to Atom.of("win")
        )
    }

    /**
     * Contains non ground Rules, with variables
     */
    val nonGroundRules by lazy {
        listOf(
                Struct.of("f", Var.anonymous()) to Atom.of("ciao"),
                Struct.of("x", Var.of("X")) to Struct.of("y", Var.of("X")),
                Struct.of("myFunc", Atom.of("a")) to Tuple.wrapIfNeeded(Var.anonymous(), Var.anonymous())
        )
    }

    /**
     * Contains mixed [groundRules] and [nonGroundRules]
     */
    val mixedRules by lazy { groundRules + nonGroundRules }
}
