package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.*

/**
 * Utils singleton for testing [Rule]
 *
 * @author Enrico
 */
internal object RuleUtils {

    /** Contains ground Rules (aka without variables) "well formed" */
    val groundWellFormedRules by lazy {
        listOf(
            Struct.of("parent", Atom.of("bob"), Atom.of("jack"))
                to Struct.of("son", Atom.of("jack"), Atom.of("bob")),
            Struct.of("money", Integer.of(100)) to Atom.of("win"),
            Atom.of("a") to Struct.of("?", Integer.of(1), Integer.of(2)),
            Atom.of("b") to Struct.of(",", Real.of(0.5), Real.of(0.6), Real.of(0.7)),
            Empty.set() to Struct.of(";", Integer.of(2)),
            Empty.list() to Struct.of("->", Integer.of(4))
        )
    }

    /** Contains non ground Rules, with variables and "well formed" */
    val nonGroundWellFormedRules by lazy {
        listOf(
            Struct.of("f", Var.anonymous()) to Atom.of("ciao"),
            Struct.of("f", Integer.of(2)) to Var.of("Ciao"),
            Struct.of("x", Var.of("X")) to Struct.of("y", Var.of("X"), Real.of(1f)),
            Struct.of("myFunc", Atom.of("a")) to Tuple.wrapIfNeeded(Var.anonymous(), Var.anonymous())
        )
    }

    /** Contains ground Rules (aka without variables) not "well-formed" */
    val groundNonWellFormedRules by lazy {
        listOf(
            Atom.of("a") to Integer.of(1),
            Atom.of("a") to Tuple.wrapIfNeeded(Real.of(2.1)),
            Atom.of("a") to Tuple.wrapIfNeeded(Atom.of("b"), Integer.of(2)),
            Atom.of("b") to Tuple.wrapIfNeeded(Real.of(0.5), Real.of(0.6), Real.of(0.7)),
            Atom.of("a") to Struct.fold(";", Truth.TRUE, Empty.list(), Real.of("2.4")),
            Atom.of("a") to Struct.fold("->", Truth.TRUE, Empty.list(), Numeric.of(2.8))
        )
    }

    /** Contains non ground Rules, with variables and not "well-formed" */
    val nonGroundNonWellFormedRules by lazy {
        listOf(
            Struct.of("A", Var.anonymous()) to Struct.of(",", Var.of("B"), Integer.of(1)),
            Atom.of("a") to Struct.fold(";", Truth.TRUE, Var.of("A"), Real.of("2.4")),
            Atom.of("a") to Struct.fold("->", Truth.TRUE, Var.anonymous(), Var.anonymous(), Numeric.of(2.8))
        )
    }

    /** Contains wellFormed rules according to [Clause.isWellFormed] */
    val wellFormedRules by lazy { groundWellFormedRules + nonGroundWellFormedRules }

    /** Contains *NOT* well-formed rules according to [Clause.isWellFormed] */
    val nonWellFormedRules by lazy { groundNonWellFormedRules + nonGroundNonWellFormedRules }

    /** Contains ground Rules (aka without variables) */
    val groundRules by lazy { groundWellFormedRules + groundNonWellFormedRules }

    /** Contains non ground Rules, with variables */
    val nonGroundRules by lazy { nonGroundWellFormedRules + nonGroundNonWellFormedRules }

    /** Contains mixed Rules, ground and non ground, well-formed and not */
    val mixedRules by lazy { groundWellFormedRules + nonGroundWellFormedRules + groundNonWellFormedRules + nonGroundNonWellFormedRules }
}
