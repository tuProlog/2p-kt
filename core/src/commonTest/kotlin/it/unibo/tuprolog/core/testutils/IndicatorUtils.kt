package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.*

/**
 * Utils singleton for testing [Indicator]
 *
 * @author Enrico
 */
internal object IndicatorUtils {

    /** Contains well-formed [Indicator]s in raw format */
    internal val rawWellFormedIndicators by lazy {
        listOf(
            "sumlist" to 2,
            "." to 2,
            "/" to 2,
            "animal" to 1,
            "1" to 0,
            "true" to 0,
            "fail" to 0,
            "[]" to 0,
            "{}" to 0
        )
    }

    /** Contains well-formed [Indicator]s according to [Indicator.isWellFormed] */
    internal val wellFormedIndicators by lazy {
        rawWellFormedIndicators.map { (name, arity) -> Atom.of(name) to Integer.of(arity) }
    }

    /** Contains non well-formed [Indicator]s, due to bad name */
    internal val nonWellFormedNameIndicator by lazy {
        listOf(
            Var.anonymous() to Integer.of(2),
            Struct.of(".", Truth.`true`()) to Integer.of(2)
        )
    }

    /** Contains non well-formed [Indicator]s, due to bad arity */
    internal val nonWellFormedArityIndicator by lazy {
        listOf(
            Atom.of("/") to Integer.of(-2),
            Atom.of("animal") to Integer.of(-1),
            Atom.of("1") to Real.of(0.5),
            Truth.`true`() to Var.anonymous()
        )
    }

    /** Contains non well-formed [Indicator]s */
    internal val nonWellFormedIndicators by lazy {
        nonWellFormedNameIndicator + nonWellFormedArityIndicator
    }

    /** Contains well-formed and non-well-formed indicators */
    internal val mixedIndicators by lazy { wellFormedIndicators + nonWellFormedIndicators }

}
