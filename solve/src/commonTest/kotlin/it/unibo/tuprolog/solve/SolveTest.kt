package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.test.*

/**
 * Test class for [Solve], [Solve.Request] and [Solve.Response]
 *
 * @author Enrico
 */
internal class SolveTest {

    private val aSignature = Signature("ciao", 2)
    private val aVarargSignature = Signature("ciao", 2, true)
    private val anArgumentList = listOf(Atom.of("a"), Truth.`true`())
    private val varargArgumentList = anArgumentList + Truth.`true`()
    private val aSolution = Solution.No(Truth.fail())
    private val anExecutionContext =
            ExecutionContext(Libraries(), emptyMap(), ClauseDatabase.empty(), ClauseDatabase.empty(), 0L)
    private val anExecutionTimeout = 300L

    private val request = Solve.Request(aSignature, anArgumentList, anExecutionContext, anExecutionTimeout)
    private val requestVararg = Solve.Request(aVarargSignature, varargArgumentList, anExecutionContext, anExecutionTimeout)

    @Test
    fun requestInsertedDataCorrect() {
        assertEquals(aSignature, request.signature)
        assertEquals(anArgumentList, request.arguments)
        assertEquals(anExecutionContext, request.context)
        assertEquals(anExecutionTimeout, request.executionTimeout)
    }

    @Test
    fun requestDefaultValuesCorrect() {
        val toBeTested = Solve.Request(aSignature, anArgumentList, anExecutionContext)

        assertEquals(Long.MAX_VALUE, toBeTested.executionTimeout)
    }

    @Test
    fun requestConstructorComplainsWithWrongArityAndArgumentsCount() {
        assertFailsWith<IllegalArgumentException> { Solve.Request(aSignature, emptyList(), anExecutionContext) }
        assertFailsWith<IllegalArgumentException> { Solve.Request(aSignature, anArgumentList + Truth.fail(), anExecutionContext) }
    }

    @Test
    fun requestConstructorComplainsWithLessThanArityArgumentsIfVarargSignature() {
        assertFailsWith<IllegalArgumentException> { Solve.Request(aVarargSignature, emptyList(), anExecutionContext) }
    }

    @Test
    fun requestConstructorPermitsMoreThanArityArgumentsIfSignatureVararg() {
        Solve.Request(aVarargSignature, anArgumentList + Truth.fail(), anExecutionContext)
    }

    @Test
    fun requestConstructorComplainsWithNegativeTimeout() {
        Solve.Request(aSignature, anArgumentList, anExecutionContext, 0)
        assertFailsWith<IllegalArgumentException> { Solve.Request(aSignature, anArgumentList, anExecutionContext, -1) }
    }

    @Test
    fun requestComputesCorrectlyQueryStructIfPossible() {
        assertEquals(Struct.of(aSignature.name, anArgumentList), request.query)
    }

    @Test
    fun requestReturnsNullQueryIfConversionNotPossible() {
        assertEquals(Struct.of(aVarargSignature.name, varargArgumentList), requestVararg.query)
    }

    @Test
    fun equalSignatureAndArgsWorksAsExpected() {
        val differentSignature = aSignature.copy(name = "myName")
        val differentArgumentList = anArgumentList.dropLast(1) + Truth.fail()
        val differentExecutionContext = anExecutionContext.copy(computationStartTime = 100)
        val differentExecutionTimeout = 20L

        // these assertion should be true before really testing this behaviour
        assertNotEquals(differentSignature, aSignature)
        assertNotEquals(differentArgumentList, anArgumentList)
        assertNotEquals(differentExecutionContext, anExecutionContext)
        assertNotEquals(differentExecutionTimeout, anExecutionTimeout)

        assertTrue { request.equalSignatureAndArgs(request.copy(context = differentExecutionContext)) }
        assertTrue { request.equalSignatureAndArgs(request.copy(executionTimeout = differentExecutionTimeout)) }
        assertFalse { request.equalSignatureAndArgs(request.copy(signature = differentSignature)) }
        assertFalse { request.equalSignatureAndArgs(request.copy(arguments = differentArgumentList)) }
    }

    @Test
    fun responseInsertedDataCorrect() {
        val toBeTested = Solve.Response(aSolution, anExecutionContext)

        assertEquals(aSolution, toBeTested.solution)
        assertEquals(anExecutionContext, toBeTested.context)
    }

}
