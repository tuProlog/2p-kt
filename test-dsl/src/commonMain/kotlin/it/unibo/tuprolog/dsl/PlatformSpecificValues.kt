package it.unibo.tuprolog.dsl

import it.unibo.tuprolog.core.Numeric

expect object PlatformSpecificValues {
    val THREE_POINT_ONE_DOUBLE: Numeric
    val THREE_POINT_ONE_FLOAT: Numeric
    val ONE_POINT_ZERO: Numeric
    val MINUS_THREE: Numeric
}
