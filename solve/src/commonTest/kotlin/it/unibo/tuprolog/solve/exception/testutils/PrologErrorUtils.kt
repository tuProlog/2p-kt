package it.unibo.tuprolog.solve.exception.testutils

import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.exception.prologerror.ErrorUtils.errorStructOf
import kotlin.test.assertEquals

/**
 * Utils singleton to help testing [PrologError]
 *
 * @author Enrico
 */
internal object PrologErrorUtils {

    /** Asserts that [PrologError.errorStruct] returns correctly constructed structure */
    internal fun assertErrorStructCorrect(prologError: PrologError) {
        assertEquals(
                prologError.extraData
                        ?.let { errorStructOf(prologError.type, it) }
                        ?: errorStructOf(prologError.type),
                prologError.errorStruct
        )
    }

}
