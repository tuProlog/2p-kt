package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.primitive.Signature
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

/**
 * Test class for [Solution], [Solution.Yes] and [Solution.No]
 *
 * @author Enrico
 */
internal class SolutionTest {

    private val myScope = Scope.empty()
    private val aQuery = with(myScope) { Struct.of("f", varOf("A")) }
    private val querySignature = Signature.fromIndicator(aQuery.indicator)!!
    private val aSubstitution = with(myScope) { Substitution.of(varOf("A"), Struct.of("c", varOf("B"))) }
    private val theQuerySolved = with(myScope) { Struct.of("f", Struct.of("c", varOf("B"))) }

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
}
