package it.unibo.tuprolog.solve.exception.error

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [ErrorUtils]
 *
 * @author Enrico
 */
internal class ErrorUtilsTest {
    private val errorDescription = Atom.of("myErrorDesc")
    private val errorExtraData = Atom.of("myExtra")
    private val defaultCustomErrorData = Atom.of("")

    @Test
    fun standardErrorWrapperFunctorCorrect() {
        assertEquals("error", ErrorUtils.ERROR_FUNCTOR)
    }

    @Test
    fun errorStructOfWithArgsCreatesCorrectStruct() {
        assertEquals(
            Struct.of(ErrorUtils.ERROR_FUNCTOR, errorDescription, errorExtraData),
            ErrorUtils.errorStructOf(errorDescription, errorExtraData),
        )
    }

    @Test
    fun errorStructOfWithOnlyFirstArgFillsExtraDataWithTrue() {
        assertEquals(
            Struct.of(ErrorUtils.ERROR_FUNCTOR, errorDescription, defaultCustomErrorData),
            ErrorUtils.errorStructOf(errorDescription),
        )
    }
}
