package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real

/**
 * JVM values (see [PlatformSpecificValues] for what each property means): the JVM `DefaultTermificator` always
 * converts a `Float`/`Double` via `Real.of(value.toString())`, so [THREE_POINT_ONE_FLOAT] carries the exact
 * decimal digits of the 32-bit float closest to `3.1` (not `"3.1"` itself, since `Float.toString` on the JVM
 * would round-trip to the shorter form), while an `Int` always becomes an [Integer] regardless of its value,
 * hence [MINUS_THREE].
 */
@Suppress("MagicNumber")
actual object PlatformSpecificValues {
    actual val THREE_POINT_ONE_FLOAT: Numeric = Real.of("3.099999904632568359375")

    actual val THREE_POINT_ONE_DOUBLE: Numeric = Real.of("3.100000000000000088817841970012523233890533447265625")

    actual val ONE_POINT_ZERO: Numeric = Real.of("1.0")

    actual val MINUS_THREE: Numeric = Integer.of(-3)
}
