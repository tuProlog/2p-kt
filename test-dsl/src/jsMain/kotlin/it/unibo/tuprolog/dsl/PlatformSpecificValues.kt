package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real

/**
 * JS values (see [PlatformSpecificValues] for what each property means): the JS `DefaultTermificator` decides
 * between [Integer] and [Real] via `NumberTypeTester`, which classifies a `Number` by matching its
 * `toString()` against a digits-only pattern, rather than by its static Kotlin type (JS has no separate
 * `Int`/`Double` representation). Two JS-specific quirks fall out of that: JS's own `Number.toString()`
 * renders a whole-valued `Double` like `1.0` as `"1"` (no decimal point), so it matches the pattern and
 * termifies to an [Integer] — see [ONE_POINT_ZERO]
 */
@Suppress("MagicNumber")
actual object PlatformSpecificValues {
    actual val THREE_POINT_ONE_FLOAT: Numeric = Real.of("3.100000000000000088817841970012523233890533447265625")

    actual val THREE_POINT_ONE_DOUBLE: Numeric = Real.of("3.100000000000000088817841970012523233890533447265625")

    actual val ONE_POINT_ZERO: Numeric = Integer.of(1)

    actual val MINUS_THREE: Numeric = Integer.of(-3)
}
