package it.unibo.tuprolog.solve.stdlib.function.testutils

/**
 * Utils singleton to help testing Float functions
 *
 * @author Enrico
 */
internal object FloatFunctionUtils {

    /** A map from an input number to the pair (integer part, fractional part) */
    internal val numbersToFloatParts = mapOf<Number, Pair<Double, Double>>(
        1 to (1.0 to 0.0),
        1.0 to (1.0 to 0.0),
        1.1 to (1.0 to 0.10),
        3.123456789101 to (3.0 to 0.123456789101),
        2.0000000001 to (2.0 to 0.0000000001),
        -3.123456789101 to (-3.0 to -0.123456789101),
        -2.0000000001 to (-2.0 to -0.0000000001),
        -1984984.02 to (-1984984.0 to -0.02)
    )
}
