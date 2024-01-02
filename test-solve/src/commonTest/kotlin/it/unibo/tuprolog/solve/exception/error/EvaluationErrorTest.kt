package it.unibo.tuprolog.solve.exception.error

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.exception.testutils.LogicErrorUtils
import it.unibo.tuprolog.solve.exception.testutils.LogicErrorUtils.assertErrorStructCorrect
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [EvaluationError]
 *
 * @author Enrico
 */
internal class EvaluationErrorTest {
    private val testErrorType = EvaluationError.Type.FLOAT_OVERFLOW
    private val aCallableTypeError =
        EvaluationError(
            context = LogicErrorUtils.aContext,
            errorType = testErrorType,
        )
    private val correctTypeFunctor = "evaluation_error"

    @Test
    fun evaluationErrorTypeStructFunctorCorrect() {
        assertEquals(correctTypeFunctor, EvaluationError.typeFunctor)
    }

    @Test
    fun evaluationErrorNameCorrect() {
        assertEquals(correctTypeFunctor, aCallableTypeError.type.functor)
    }

    @Test
    fun evaluationErrorTypeCorrect() {
        assertEquals(
            Struct.of(correctTypeFunctor, testErrorType.toTerm()),
            aCallableTypeError.type,
        )
    }

    @Test
    fun evaluationErrorToThrowStructCorrect() {
        assertErrorStructCorrect(aCallableTypeError)
    }

    @Test
    fun evaluationErrorErrorTypeCorrect() {
        assertEquals(testErrorType, aCallableTypeError.errorType)
    }
}
