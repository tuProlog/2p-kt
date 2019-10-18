package it.unibo.tuprolog.solve.exception

import it.unibo.tuprolog.solve.exception.testutils.PrologErrorUtils
import it.unibo.tuprolog.solve.exception.testutils.PrologErrorUtils.aCause
import it.unibo.tuprolog.solve.exception.testutils.PrologErrorUtils.aContext
import it.unibo.tuprolog.solve.exception.testutils.PrologErrorUtils.aDifferentContext
import it.unibo.tuprolog.solve.exception.testutils.PrologErrorUtils.aMessage
import it.unibo.tuprolog.solve.exception.testutils.PrologErrorUtils.aType
import it.unibo.tuprolog.solve.exception.testutils.PrologErrorUtils.assertEqualPrologErrorData
import it.unibo.tuprolog.solve.exception.testutils.PrologErrorUtils.assertErrorStructCorrect
import it.unibo.tuprolog.solve.exception.testutils.PrologErrorUtils.someExtraData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Test class for [PrologError]
 *
 * @author Enrico
 */
internal class PrologErrorTest {

    /** A non specific PrologError instance */
    private val underTestPrologError = object : PrologError(aMessage, aCause, aContext, aType, someExtraData) {}

    /** Specific prolog error instances */
    private val prologErrorTypeToInstanceMap = PrologErrorUtils.recognizedSubTypes.map { (typeParam, _) ->
        typeParam to PrologError.of(aMessage, aCause, aContext, typeParam, someExtraData)
    }

    @Test
    fun holdsInsertedData() {
        assertEqualPrologErrorData(aMessage, aCause, aContext, aType, someExtraData, underTestPrologError)
    }

    @Test
    fun defaultsAreNull() {
        val toBeTested = object : PrologError(context = aContext, type = aType) {}
        assertNull(toBeTested.message)
        assertNull(toBeTested.cause)
        assertNull(toBeTested.extraData)
    }

    @Test
    fun constructorInsertsMessageIfOnlyCauseSpecified() {
        val prologError = object : PrologError(aCause, aContext, aType, someExtraData) {}

        assertEquals(aCause.toString(), prologError.message)
    }

    @Test
    fun errorIsComputedCorrectly() {
        assertErrorStructCorrect(underTestPrologError)
        assertErrorStructCorrect(object : PrologError(context = aContext, type = aType) {})
    }

    @Test
    fun updateContextReturnsExceptionWithSameContentsButUpdatedContext() {
        val allPrologErrorInstances = prologErrorTypeToInstanceMap + (aType to underTestPrologError)

        allPrologErrorInstances.forEach { (type, prologError) ->
            val toBeTested = prologError.updateContext(aDifferentContext)

            assertEqualPrologErrorData(aMessage, aCause, aDifferentContext, type, someExtraData, toBeTested)
        }
    }

    @Test
    fun ofCreatesCorrectSubInstanceIfDetected() {
        PrologErrorUtils.recognizedSubTypes.forEach { (typeParam, expectedType) ->
            val actualError = PrologError.of(context = aContext, type = typeParam)
            assertEquals(expectedType, actualError::class)
        }
    }
}
