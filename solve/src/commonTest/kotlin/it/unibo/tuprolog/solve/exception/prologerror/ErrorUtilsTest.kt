package it.unibo.tuprolog.solve.exception.prologerror

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
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

    @Test
    fun standardErrorWrapperFunctorCorrect() {
        assertEquals("error", ErrorUtils.errorWrapperFunctor)
    }

    @Test
    fun errorStructOfWithArgsCreatesCorrectStruct() {
        assertEquals(
                Struct.of(ErrorUtils.errorWrapperFunctor, errorDescription, errorExtraData),
                ErrorUtils.errorStructOf(errorDescription, errorExtraData)
        )
    }

    @Test
    fun errorStructOfWithOnlyFirstArgFillsExtraDataWithTrue() {
        assertEquals(
                Struct.of(ErrorUtils.errorWrapperFunctor, errorDescription, Truth.`true`()),
                ErrorUtils.errorStructOf(errorDescription)
        )
    }
}
