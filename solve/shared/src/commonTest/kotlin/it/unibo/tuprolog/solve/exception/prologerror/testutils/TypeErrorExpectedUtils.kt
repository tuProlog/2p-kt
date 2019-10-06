package it.unibo.tuprolog.solve.exception.prologerror.testutils

import it.unibo.tuprolog.solve.exception.prologerror.TypeError

/**
 * Utils singleton to help testing [TypeError.Expected]
 *
 * @author Enrico
 */
internal object TypeErrorExpectedUtils {

    /** A map from predefined error names to predefined error instances */
    internal val predefinedErrorNamesToInstances by lazy {
        listOf("callable", "atom", "integer", "number", "predicate_indicator", "compound", "list", "character")
                .map { it to TypeError.Expected.of(it) }
                .toMap()
    }

    /** A map from non-predefined error names to their instances */
    internal val nonPredefinedToInstances by lazy {
        listOf("myString!", "non_predefined_error")
                .map { it to TypeError.Expected.of(it) }
                .toMap()
    }

    /** A map from names to instance for all expected types under test */
    internal val allNamesToInstances by lazy {
        predefinedErrorNamesToInstances + nonPredefinedToInstances
    }

}
