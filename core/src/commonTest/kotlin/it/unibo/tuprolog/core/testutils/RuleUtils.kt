package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Empty
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var

/**
 * Utils singleton for testing [Rule]
 *
 * @author Enrico
 */
internal object RuleUtils {
    /** A named [Rule] head and body pair, for parameterized tests */
    internal data class RuleCase(
        val id: String,
        val head: Struct,
        val body: Term,
    )

    /** Contains ground Rules (aka without variables) "well formed" */
    val groundWellFormedRuleCases by lazy {
        listOf(
            RuleCase(
                "parent-if-son",
                Struct.of("parent", Atom.of("bob"), Atom.of("jack")),
                Struct.of("son", Atom.of("jack"), Atom.of("bob")),
            ),
            RuleCase("money-if-win", Struct.of("money", Integer.of(100)), Atom.of("win")),
            RuleCase("question-mark-body", Atom.of("a"), Struct.of("?", Integer.of(1), Integer.of(2))),
            RuleCase("comma-3-body", Atom.of("b"), Struct.of(",", Real.of(0.5), Real.of(0.6), Real.of(0.7))),
            RuleCase("empty-block-head", Empty.block(), Struct.of(";", Integer.of(2))),
            RuleCase("empty-list-head", Empty.list(), Struct.of("->", Integer.of(4))),
        )
    }

    /** Contains non ground Rules, with variables and "well formed" */
    val nonGroundWellFormedRuleCases by lazy {
        listOf(
            RuleCase("anonymous-variable-in-head", Struct.of("f", Var.anonymous()), Atom.of("ciao")),
            RuleCase("variable-body", Struct.of("f", Integer.of(2)), Var.of("Ciao")),
            RuleCase(
                "variables-in-head-and-body",
                Struct.of("x", Var.of("X")),
                Struct.of("y", Var.of("X"), Real.of(1f)),
            ),
            RuleCase(
                "tuple-of-anonymous-variables-body",
                Struct.of("myFunc", Atom.of("a")),
                Tuple.wrapIfNeeded(Var.anonymous(), Var.anonymous()),
            ),
        )
    }

    /** Contains ground Rules (aka without variables) not "well-formed" */
    val groundNonWellFormedRuleCases by lazy {
        listOf(
            RuleCase("integer-body", Atom.of("a"), Integer.of(1)),
            RuleCase("real-body", Atom.of("a"), Tuple.wrapIfNeeded(Real.of(2.1))),
            RuleCase("tuple-with-integer-body", Atom.of("a"), Tuple.wrapIfNeeded(Atom.of("b"), Integer.of(2))),
            RuleCase(
                "tuple-of-reals-body",
                Atom.of("b"),
                Tuple.wrapIfNeeded(Real.of(0.5), Real.of(0.6), Real.of(0.7)),
            ),
            RuleCase(
                "semicolon-with-real-body",
                Atom.of("a"),
                Struct.fold(";", Truth.TRUE, Empty.list(), Real.of("2.4")),
            ),
            RuleCase(
                "arrow-with-real-body",
                Atom.of("a"),
                Struct.fold("->", Truth.TRUE, Empty.list(), Numeric.of(2.8)),
            ),
        )
    }

    /** Contains non ground Rules, with variables and not "well-formed" */
    val nonGroundNonWellFormedRuleCases by lazy {
        listOf(
            RuleCase(
                "comma-with-integer-body",
                Struct.of("A", Var.anonymous()),
                Struct.of(",", Var.of("B"), Integer.of(1)),
            ),
            RuleCase(
                "semicolon-with-variable-body",
                Atom.of("a"),
                Struct.fold(";", Truth.TRUE, Var.of("A"), Real.of("2.4")),
            ),
            RuleCase(
                "arrow-with-anonymous-variables-body",
                Atom.of("a"),
                Struct.fold("->", Truth.TRUE, Var.anonymous(), Var.anonymous(), Numeric.of(2.8)),
            ),
        )
    }

    /** Contains wellFormed rules according to [Clause.isWellFormed] */
    val wellFormedRuleCases by lazy { groundWellFormedRuleCases + nonGroundWellFormedRuleCases }

    /** Contains *NOT* well-formed rules according to [Clause.isWellFormed] */
    val nonWellFormedRuleCases by lazy { groundNonWellFormedRuleCases + nonGroundNonWellFormedRuleCases }

    /** Contains ground Rules (aka without variables) */
    val groundRuleCases by lazy { groundWellFormedRuleCases + groundNonWellFormedRuleCases }

    /** Contains non ground Rules, with variables */
    val nonGroundRuleCases by lazy { nonGroundWellFormedRuleCases + nonGroundNonWellFormedRuleCases }

    /** Contains mixed Rules, ground and non ground, well-formed and not */
    val mixedRuleCases by lazy {
        groundWellFormedRuleCases +
            nonGroundWellFormedRuleCases +
            groundNonWellFormedRuleCases +
            nonGroundNonWellFormedRuleCases
    }
}
