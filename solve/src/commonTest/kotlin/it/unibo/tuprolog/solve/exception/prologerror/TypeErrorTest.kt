package it.unibo.tuprolog.solve.exception.prologerror

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.solve.exception.testutils.PrologErrorUtils
import it.unibo.tuprolog.solve.testutils.DummyInstances
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
            context = DummyInstances.executionContext,
            expectedType = testErrorType,
            actualValue = callableTypeErrorActualValue
    )

    @Test
    fun typeErrorNameCorrect() {
        assertEquals("type_error", aCallableTypeError.type.functor)
    }

    @Test
    fun typeErrorTypeCorrect() {
        assertEquals(
                Struct.of("type_error", testErrorType.toAtom(), callableTypeErrorActualValue),
                aCallableTypeError.type
        )
    }

    @Test
    fun typeErrorToThrowStructCorrect() {
        PrologErrorUtils.assertErrorStructCorrect(aCallableTypeError)
    }

    @Test
    fun typeErrorExpectedTypeCorrect() {
        assertEquals(testErrorType, aCallableTypeError.expectedType)
    }

    @Test
    fun typeErrorActualValueCorrect() {
        assertEquals(callableTypeErrorActualValue, aCallableTypeError.actualValue)
    }

    @Test
    fun typeErrorExpectedEnumToAtomWorksAsExpected() {
        TypeError.Expected.values().forEach {
            assertEquals(Atom.of(it.toString().toLowerCase()), it.toAtom())
        }
    }
}
