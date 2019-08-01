package it.unibo.tuprolog.core.testutils

import it.unibo.tuprolog.core.*

/**
 * Utils singleton for testing [Indicator]
 *
 * @author Enrico
 */
internal object IndicatorUtils {

    /** Contains well-formed [Indicator]s according to [Indicator.isWellFormed] */
    internal val wellFormedIndicators by lazy {
        listOf(
                Atom.of("sumlist") to Integer.of(2),
                Atom.of(".") to Integer.of(2),
                Atom.of("/") to Integer.of(2),
                Atom.of("animal") to Integer.of(1),
                Atom.of("1") to Integer.of(0),
                Truth.`true`() to Integer.of(0),
                Truth.fail() to Integer.of(0),
                Empty.list() to Integer.of(0),
                Empty.set() to Integer.of(0)
        )
    }

    /** Contains non well-formed [Indicator]s */
    internal val nonWellFormedIndicators by lazy {
        listOf(
                Var.anonymous() to Integer.of(2),
                Struct.of(".", Truth.`true`()) to Integer.of(2),
                Atom.of("/") to Integer.of(-2),
                Atom.of("animal") to Integer.of(-1),
                Atom.of("1") to Real.of(0.5),
                Truth.`true`() to Var.anonymous()
        )
    }

    /** Contains well-formed and non-well-formed indicators */
    internal val mixedIndicators by lazy { wellFormedIndicators + nonWellFormedIndicators }

}
