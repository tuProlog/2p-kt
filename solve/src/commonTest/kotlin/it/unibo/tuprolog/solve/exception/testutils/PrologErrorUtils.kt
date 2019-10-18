package it.unibo.tuprolog.solve.exception.testutils

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.exception.prologerror.ErrorUtils.errorStructOf
import it.unibo.tuprolog.solve.exception.prologerror.InstantiationError
import it.unibo.tuprolog.solve.exception.prologerror.SystemError
import it.unibo.tuprolog.solve.exception.prologerror.TypeError
import kotlin.test.assertEquals

/**
 * Utils singleton to help testing [PrologError]
 *
 * @author Enrico
 */
internal object PrologErrorUtils {

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
                Struct.of(TypeError.typeFunctor, Atom.of("callable"), Atom.of("someActualValue")) to TypeError::class
        )
    }

    /** Utility function to check if exception contains same expected values */
    internal fun assertEqualPrologErrorData(
            expectedMessage: String?,
            expectedCause: Throwable?,
            expectedContext: ExecutionContext,
            expectedType: Struct,
            expectedExtraData: Term?,
            actualException: PrologError
    ) {
        TuPrologRuntimeExceptionUtils.assertSameMessageCauseContext(expectedMessage, expectedCause, expectedContext, actualException)
        assertEquals(expectedType, actualException.type)
        assertEquals(expectedExtraData, actualException.extraData)
    }

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
