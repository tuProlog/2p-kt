package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real

@Suppress("MagicNumber")
actual object PlatformSpecificValues {
    actual val THREE_POINT_ONE_FLOAT: Numeric = Real.of("3.099999904632568359375")

    actual val THREE_POINT_ONE_DOUBLE: Numeric = Real.of("3.100000000000000088817841970012523233890533447265625")

    actual val ONE_POINT_ZERO: Numeric = Real.of("1.0")

    actual val MINUS_THREE: Numeric = Integer.of(-3)
}
