package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.testutils.DummyInstances
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

/**
 * Test class for [Solution], [Solution.Yes], [Solution.No] and [Solution.Halt]
 *
 * @author Enrico
 */
internal class SolutionTest {

    private val myScope = Scope.empty()
    private val aQuery = with(myScope) { Struct.of("f", varOf("A")) }
    private val querySignature = aQuery.extractSignature()
    private val aSubstitution = with(myScope) { Substitution.of(varOf("A"), Struct.of("c", varOf("B"))) }
    private val theQuerySolved = with(myScope) { Struct.of("f", Struct.of("c", varOf("B"))) }
    private val anException = HaltException(context = DummyInstances.executionContext)

    @Test
    fun yesSolutionContainsInsertedData() {
        val toBeTested = Solution.Yes(aQuery, aSubstitution)

        assertEquals(aQuery, toBeTested.query)
        assertEquals(aSubstitution, toBeTested.substitution)
    }

    @Test
    fun yesSolutionComputesSolvedQueryApplyingSubstitutionToQuery() {
        val toBeTested = Solution.Yes(aQuery, aSubstitution)

        assertEquals(theQuerySolved, toBeTested.solvedQuery)
    }

    @Test
    fun yesSolutionSecondaryConstructorWorksAsExpected() {
        val toBeTested = Solution.Yes(querySignature, listOf(with(myScope) { varOf("A") }), aSubstitution)

        assertEquals(aQuery, toBeTested.query)
        assertEquals(aSubstitution, toBeTested.substitution)
        assertEquals(theQuerySolved, toBeTested.solvedQuery)
    }

    @Test
    fun yesSolutionSecondaryConstructorComplainsIfArityDoesntMatchArgumentsCount() {
        assertFailsWith<IllegalArgumentException> { Solution.Yes(querySignature, emptyList(), Substitution.empty()) }
        assertFailsWith<IllegalArgumentException> { Solution.Yes(querySignature, listOf(Truth.`true`(), Truth.`true`()), Substitution.empty()) }
    }

    @Test
    fun yesSolutionSecondaryConstructorComplainsIfSignatureVararg() {
        assertFailsWith<IllegalArgumentException> {
            Solution.Yes(querySignature.copy(vararg = true), listOf(with(myScope) { varOf("A") }), Substitution.empty())
        }
    }

    @Test
    fun noSolutionContainsInsertedData() {
        assertEquals(aQuery, Solution.No(aQuery).query)
    }

    @Test
    fun noSolutionSolutionAlwaysNull() {
        assertNull(Solution.No(aQuery).solvedQuery)
    }

    @Test
    fun noSolutionSubstitutionAlwaysFailed() {
        assertEquals(Substitution.failed(), Solution.No(aQuery).substitution)
    }

    @Test
    fun noSolutionSecondaryConstructorWorksAsExpected() {
        val toBeTested = Solution.No(querySignature, listOf(with(myScope) { varOf("A") }))

        assertEquals(aQuery, toBeTested.query)
    }

    @Test
    fun noSolutionSecondaryConstructorComplainsIfArityDoesntMatchArgumentsCount() {
        assertFailsWith<IllegalArgumentException> { Solution.No(querySignature, emptyList()) }
        assertFailsWith<IllegalArgumentException> { Solution.No(querySignature, listOf(Truth.`true`(), Truth.`true`())) }
    }

    @Test
    fun noSolutionSecondaryConstructorComplainsIfSignatureVararg() {
        assertFailsWith<IllegalArgumentException> {
            Solution.No(querySignature.copy(vararg = true), listOf(with(myScope) { varOf("A") }))
        }
    }

    @Test
    fun haltSolutionContainsInsertedData() {
        val toBeTested = Solution.Halt(aQuery, anException)

        assertEquals(aQuery, toBeTested.query)
        assertEquals(anException, toBeTested.exception)
    }

    @Test
    fun haltSolutionSolutionAlwaysNull() {
        assertNull(Solution.Halt(aQuery, anException).solvedQuery)
    }

    @Test
    fun haltSolutionSubstitutionAlwaysFailed() {
        assertEquals(Substitution.failed(), Solution.Halt(aQuery, anException).substitution)
    }

    @Test
    fun haltSolutionSecondaryConstructorWorksAsExpected() {
        val toBeTested = Solution.Halt(querySignature, listOf(with(myScope) { varOf("A") }), anException)

        assertEquals(aQuery, toBeTested.query)
    }

    @Test
    fun haltSolutionSecondaryConstructorComplainsIfArityDoesntMatchArgumentsCount() {
        assertFailsWith<IllegalArgumentException> { Solution.Halt(querySignature, emptyList(), anException) }
        assertFailsWith<IllegalArgumentException> { Solution.Halt(querySignature, listOf(Truth.`true`(), Truth.`true`()), anException) }
    }

    @Test
    fun haltSolutionSecondaryConstructorComplainsIfSignatureVararg() {
        assertFailsWith<IllegalArgumentException> {
            Solution.Halt(querySignature.copy(vararg = true), listOf(with(myScope) { varOf("A") }), anException)
        }
    }
}
