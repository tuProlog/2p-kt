package it.unibo.tuprolog.solve.exception.error

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.exception.testutils.PrologErrorUtils
import it.unibo.tuprolog.solve.exception.testutils.PrologErrorUtils.assertErrorStructCorrect
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [TypeError]
 *
 * @author Enrico
 */
internal class TypeErrorTest {

    private val callableTypeErrorActualValue = Integer.of(1)
    private val testErrorType = TypeError.Expected.CALLABLE
    private val aCallableTypeError = TypeError(
        context = PrologErrorUtils.aContext,
        expectedType = testErrorType,
        actualValue = callableTypeErrorActualValue
    )
    private val correctTypeFunctor = "type_error"

    @Test
    fun typeErrorTypeStructFunctorCorrect() {
        assertEquals(correctTypeFunctor, TypeError.typeFunctor)
    }

    @Test
    fun typeErrorNameCorrect() {
        assertEquals(correctTypeFunctor, aCallableTypeError.type.functor)
    }

    @Test
    fun typeErrorTypeCorrect() {
        assertEquals(
            Struct.of(correctTypeFunctor, testErrorType.toTerm(), callableTypeErrorActualValue),
            aCallableTypeError.type
        )
    }

    @Test
    fun typeErrorToThrowStructCorrect() {
        assertErrorStructCorrect(aCallableTypeError)
    }

    @Test
    fun typeErrorExpectedTypeCorrect() {
        assertEquals(testErrorType, aCallableTypeError.expectedType)
    }

    @Test
    fun typeErrorActualValueCorrect() {
        assertEquals(callableTypeErrorActualValue, aCallableTypeError.actualValue)
    }
}
