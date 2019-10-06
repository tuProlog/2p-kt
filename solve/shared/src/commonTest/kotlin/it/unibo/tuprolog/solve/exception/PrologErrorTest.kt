package it.unibo.tuprolog.solve.exception

import it.unibo.tuprolog.solve.exception.testutils.PrologErrorUtils
import it.unibo.tuprolog.solve.exception.testutils.PrologErrorUtils.aCause
import it.unibo.tuprolog.solve.exception.testutils.PrologErrorUtils.aContext
import it.unibo.tuprolog.solve.exception.testutils.PrologErrorUtils.aMessage
import it.unibo.tuprolog.solve.exception.testutils.PrologErrorUtils.aType
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

    private val underTestPrologError = object : PrologError(aMessage, aCause, aContext, aType, someExtraData) {}

    @Test
    fun holdsInsertedData() {
        assertEquals(aMessage, underTestPrologError.message)
        assertEquals(aCause, underTestPrologError.cause)
        assertEquals(aContext, underTestPrologError.context)
        assertEquals(aType, underTestPrologError.type)
        assertEquals(someExtraData, underTestPrologError.extraData)
    }

    @Test
    fun defaultsAreNull() {
        val toBeTested = object : PrologError(context = aContext, type = aType) {}
        assertNull(toBeTested.message)
        assertNull(toBeTested.cause)
        assertNull(toBeTested.extraData)
    }

    @Test
    fun errorIsComputedCorrectly() {
        assertErrorStructCorrect(underTestPrologError)
        assertErrorStructCorrect(object : PrologError(context = aContext, type = aType) {})
    }

    @Test
    fun ofCreatesCorrectSubInstanceIfDetected() {
        PrologErrorUtils.recognizedSubTypes.forEach { (typeParam, expectedType) ->
            val actualError = PrologError.of(context = aContext, type = typeParam)
            assertEquals(expectedType, actualError::class)
        }
    }
}
