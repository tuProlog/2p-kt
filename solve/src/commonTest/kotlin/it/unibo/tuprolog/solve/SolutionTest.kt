package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.solve.testutils.SolutionUtils.aQuery
import it.unibo.tuprolog.solve.testutils.SolutionUtils.aSubstitution
import it.unibo.tuprolog.solve.testutils.SolutionUtils.anException
import it.unibo.tuprolog.solve.testutils.SolutionUtils.queryArgList
import it.unibo.tuprolog.solve.testutils.SolutionUtils.querySignature
import it.unibo.tuprolog.solve.testutils.SolutionUtils.theQuerySolved
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

    @Test
    fun yesSolutionContainsInsertedData() {
        val toBeTested = Solution.Yes(aQuery, aSubstitution)

        assertEquals(aQuery, toBeTested.query)
        assertEquals(aSubstitution, toBeTested.substitution)
    }

    @Test
    fun yesSolutionSubstitutionDefaultsToEmptyIfNotSpecified() {
        val toBeTested = Solution.Yes(aQuery)

        assertEquals(Substitution.empty(), toBeTested.substitution)
    }

    @Test
    fun yesSolutionComputesSolvedQueryApplyingSubstitutionToQuery() {
        val toBeTested = Solution.Yes(aQuery, aSubstitution)

        assertEquals(theQuerySolved, toBeTested.solvedQuery)
    }

    @Test
    fun yesSolutionSecondaryConstructorWorksAsExpected() {
        val toBeTested = Solution.Yes(querySignature, queryArgList, aSubstitution)

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
            Solution.Yes(querySignature.copy(vararg = true), queryArgList, Substitution.empty())
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
        val toBeTested = Solution.No(querySignature, queryArgList)

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
            Solution.No(querySignature.copy(vararg = true), queryArgList)
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
        val toBeTested = Solution.Halt(querySignature, queryArgList, anException)

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
            Solution.Halt(querySignature.copy(vararg = true), queryArgList, anException)
        }
    }
}
