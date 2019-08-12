package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.theory.ClauseDatabase
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [Solve], [Solve.Request] and [Solve.Response]
 *
 * @author Enrico
 */
internal class SolveTest {

    private val aSignature = Signature("ciao", 2)
    private val anArgumentList = listOf(Atom.of("a"), Truth.`true`())
    private val aSolution = Solution.No(Truth.fail())
    private val anExecutionContext = ExecutionContext(Libraries(), emptyMap(), ClauseDatabase.of(), ClauseDatabase.of())

    @Test
    fun requestInsertedDataCorrect() {
        val toBeTested = Solve.Request(aSignature, anArgumentList, anExecutionContext)

        assertEquals(aSignature, toBeTested.signature)
        assertEquals(anArgumentList, toBeTested.arguments)
        assertEquals(anExecutionContext, toBeTested.context)
    }

    @Test
    fun responseInsertedDataCorrect() {
        val toBeTested = Solve.Response(aSolution, anExecutionContext)

        assertEquals(aSolution, toBeTested.solution)
        assertEquals(anExecutionContext, toBeTested.context)
    }

    @Test
    fun responseExecutionContextCanBeOmittedAndItsNullThen() {
        assertEquals(null, Solve.Response(aSolution).context)
    }

}
