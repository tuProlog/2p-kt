package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Numeric

/**
 * Expected [Numeric] results, per Kotlin platform, for a handful of numeric literals whose termification
 * ([Termificator.termify]) is not platform-independent: the JVM and JS backends don't always agree on the exact
 * decimal digits a floating-point value round-trips to, nor on whether a mathematically-integral `Double`/`Int`
 * ends up as an [it.unibo.tuprolog.core.Integer] or a [it.unibo.tuprolog.core.Real] — see the JVM and JS `actual`
 * declarations for the concrete values, and `TestLegacyTermifier`/`TestMinimalLogicProgrammingScope` (in
 * `:test-dsl`'s own test suite) for how they are used.
 */
expect object PlatformSpecificValues {
    /** What `3.1` as a `Double` termifies to on this platform. */
    val THREE_POINT_ONE_DOUBLE: Numeric

    /** What `3.1` as a `Float` termifies to on this platform (its 32-bit representation is not exactly `3.1`). */
    val THREE_POINT_ONE_FLOAT: Numeric

    /** What `1.0` as a `Double` termifies to on this platform. */
    val ONE_POINT_ZERO: Numeric

    /** What `-3` as an `Int` termifies to on this platform. */
    val MINUS_THREE: Numeric
}
