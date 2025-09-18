package it.unibo.tuprolog.solve.exception.error.testutils

import it.unibo.tuprolog.solve.exception.error.TypeError

/**
 * Utils singleton to help testing [TypeError.Expected]
 *
 * @author Enrico
 */
internal object TypeErrorExpectedUtils {
    /** A map from predefined error names to predefined error instances */
    internal val predefinedErrorNamesToInstances by lazy {
        TypeError.Expected
            .values()
            .map { it.toString() to it }
            .toMap()
    }

    /** A map from names to instance for all expected types under test */
    internal val allNamesToInstances by lazy {
        predefinedErrorNamesToInstances
    }
}
