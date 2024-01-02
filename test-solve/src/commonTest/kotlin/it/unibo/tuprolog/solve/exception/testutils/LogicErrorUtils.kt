package it.unibo.tuprolog.solve.exception.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.LogicError
import it.unibo.tuprolog.solve.exception.error.ErrorUtils.errorStructOf
import it.unibo.tuprolog.solve.exception.error.EvaluationError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.exception.error.SystemError
import it.unibo.tuprolog.solve.exception.error.TypeError
import kotlin.test.assertEquals

/**
 * Utils singleton to help testing [LogicError]
 *
 * @author Enrico
 */
@Suppress("ConstPropertyName", "ktlint:standard:property-naming")
internal object LogicErrorUtils {
    internal const val aMessage = TuPrologRuntimeExceptionUtils.aMessage
    internal val aCause = TuPrologRuntimeExceptionUtils.aCause
    internal val aContext = TuPrologRuntimeExceptionUtils.aContext
    internal val aType = Atom.of("myType")
    internal val someExtraData = Atom.of("extra")

    internal val aDifferentContext = TuPrologRuntimeExceptionUtils.aDifferentContext

    /** Contains PrologErrors subclasses that will be recognized by type parameter */
    internal val recognizedSubTypes by lazy {
        mapOf(
            Atom.of(InstantiationError.typeFunctor) to InstantiationError::class,
            Atom.of(SystemError.typeFunctor) to SystemError::class,
            Struct.of(TypeError.typeFunctor, Atom.of("callable"), Atom.of("someActualValue")) to TypeError::class,
            Struct.of(EvaluationError.typeFunctor, Atom.of("zero_divisor")) to EvaluationError::class,
        )
    }

    /** Utility function to check if exception contains same expected values */
    internal fun assertEqualPrologErrorData(
        expectedMessage: String?,
        expectedCause: Throwable?,
        expectedContext: ExecutionContext,
        expectedType: Struct,
        expectedExtraData: Term?,
        actualException: LogicError,
    ) {
        TuPrologRuntimeExceptionUtils.assertSameMessageCauseContext(
            expectedMessage,
            expectedCause,
            expectedContext,
            actualException,
        )
        assertEquals(expectedType, actualException.type)
        assertEquals(expectedExtraData, actualException.extraData)
    }

    /** Asserts that [LogicError.errorStruct] returns correctly constructed structure */
    internal fun assertErrorStructCorrect(logicError: LogicError) {
        assertEquals(
            logicError.extraData
                ?.let { errorStructOf(logicError.type, it) }
                ?: errorStructOf(logicError.type),
            logicError.errorStruct,
        )
    }
}
