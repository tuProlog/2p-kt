package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Real

actual object PlatformSpecificValues {
    actual val THREE_POINT_ONE_FLOAT: Numeric = Real.of("3.0999999046325684")

    actual val ONE_POINT_ZERO: Numeric = Real.of("1.0")
}