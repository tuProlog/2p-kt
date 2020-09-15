package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real

actual object PlatformSpecificValues {
    actual val THREE_POINT_ONE_FLOAT: Numeric = Real.of("3.1")

    actual val ONE_POINT_ZERO: Numeric = Integer.of(1)

    actual val MINUS_THREE: Numeric = Real.of("-3.0")
}
