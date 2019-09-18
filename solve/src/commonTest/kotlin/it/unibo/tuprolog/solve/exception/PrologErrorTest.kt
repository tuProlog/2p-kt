package it.unibo.tuprolog.solve.exception

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.solve.exception.testutils.PrologErrorUtils
import it.unibo.tuprolog.solve.testutils.DummyInstances
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/**
 * Test class for [PrologError]
 *
 * @author Enrico
 */
internal class PrologErrorTest {

    private val testMessage = "ciao"
    private val testCause = IllegalArgumentException()
    private val testContext = DummyInstances.executionContext
    private val testType = Atom.of("myType")
    private val testExtraData = Atom.of("extra")

    private val underTestPrologError = object : PrologError(testMessage, testCause, testContext, testType, testExtraData) {}

    @Test
    fun holdsInsertedData() {
        assertEquals(testMessage, underTestPrologError.message)
        assertEquals(testCause, underTestPrologError.cause)
        assertEquals(testContext, underTestPrologError.context)
        assertEquals(testType, underTestPrologError.type)
        assertEquals(testExtraData, underTestPrologError.extraData)
    }

    @Test
    fun defaultsAreNull() {
        val toBeTested = object : PrologError(context = DummyInstances.executionContext, type = testType) {}
        assertNull(toBeTested.message)
        assertNull(toBeTested.cause)
        assertNull(toBeTested.extraData)
    }

    @Test
    fun errorIsComputedCorrectly() {
        PrologErrorUtils.assertErrorStructCorrect(underTestPrologError)
        PrologErrorUtils.assertErrorStructCorrect(object : PrologError(context = DummyInstances.executionContext, type = testType) {})
    }

    @Test
    fun ofCreatesCorrectSubInstanceIfDetected() {
        PrologErrorUtils.recognizedSubTypes.forEach { (typeParam, expectedType) ->
            val actualError = PrologError.of(context = DummyInstances.executionContext, type = typeParam)
            assertEquals(expectedType, actualError::class)
        }
    }
}
