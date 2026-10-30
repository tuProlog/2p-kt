package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Var

/**
 * Utils singleton for testing [Indicator]
 *
 * @author Enrico
 */
internal object IndicatorUtils {
    /** A named pair of [Indicator] components, for parameterized tests */
    internal data class IndicatorCase(
        val id: String,
        val name: Term,
        val arity: Term,
    ) {
        val pair: Pair<Term, Term> get() = name to arity
    }

    /** Well-formed [Indicator]s according to [Indicator.isWellFormed] */
    internal val wellFormedIndicatorCases by lazy {
        listOf(
            IndicatorCase("well-formed/sumlist-2", Atom.of("sumlist"), Integer.of(2)),
            IndicatorCase("well-formed/dot-2", Atom.of("."), Integer.of(2)),
            IndicatorCase("well-formed/slash-2", Atom.of("/"), Integer.of(2)),
            IndicatorCase("well-formed/animal-1", Atom.of("animal"), Integer.of(1)),
            IndicatorCase("well-formed/quoted-number-0", Atom.of("1"), Integer.of(0)),
            IndicatorCase("well-formed/true-0", Atom.of("true"), Integer.of(0)),
            IndicatorCase("well-formed/fail-0", Atom.of("fail"), Integer.of(0)),
            IndicatorCase("well-formed/empty-list-0", Atom.of("[]"), Integer.of(0)),
            IndicatorCase("well-formed/empty-block-0", Atom.of("{}"), Integer.of(0)),
        )
    }

    /** Non well-formed [Indicator]s, due to bad name */
    internal val nonWellFormedNameIndicatorCases by lazy {
        listOf(
            IndicatorCase("bad-name/anonymous-variable", Var.anonymous(), Integer.of(2)),
            IndicatorCase("bad-name/compound", Struct.of(".", Truth.TRUE), Integer.of(2)),
        )
    }

    /** Non well-formed [Indicator]s, due to bad arity */
    internal val nonWellFormedArityIndicatorCases by lazy {
        listOf(
            IndicatorCase("bad-arity/negative-2", Atom.of("/"), Integer.of(-2)),
            IndicatorCase("bad-arity/negative-1", Atom.of("animal"), Integer.of(-1)),
            IndicatorCase("bad-arity/real", Atom.of("1"), Real.of(0.5)),
            IndicatorCase("bad-arity/anonymous-variable", Truth.TRUE, Var.anonymous()),
        )
    }

    /** Well-formed and non-well-formed indicators */
    internal val mixedIndicatorCases by lazy {
        wellFormedIndicatorCases + nonWellFormedNameIndicatorCases + nonWellFormedArityIndicatorCases
    }

    /** Contains well-formed [Indicator]s in raw format */
    internal val rawWellFormedIndicators by lazy {
        wellFormedIndicatorCases.map {
            it.name.castToAtom().value to
                it.arity
                    .castToInteger()
                    .intValue
                    .toInt()
        }
    }

    /** Contains well-formed [Indicator]s according to [Indicator.isWellFormed] */
    internal val wellFormedIndicators by lazy { wellFormedIndicatorCases.map { it.pair } }

    /** Contains non well-formed [Indicator]s, due to bad name */
    internal val nonWellFormedNameIndicator by lazy { nonWellFormedNameIndicatorCases.map { it.pair } }

    /** Contains non well-formed [Indicator]s, due to bad arity */
    internal val nonWellFormedArityIndicator by lazy { nonWellFormedArityIndicatorCases.map { it.pair } }

    /** Contains non well-formed [Indicator]s */
    internal val nonWellFormedIndicators by lazy {
        nonWellFormedNameIndicator + nonWellFormedArityIndicator
    }

    /** Contains well-formed and non-well-formed indicators */
    internal val mixedIndicators by lazy { mixedIndicatorCases.map { it.pair } }
}
