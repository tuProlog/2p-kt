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
        val toBeTested = Solution.yes(aQuery, aSubstitution)

        assertEquals(aQuery, toBeTested.query)
        assertEquals(aSubstitution, toBeTested.substitution)
    }

    @Test
    fun yesSolutionSubstitutionDefaultsToEmptyIfNotSpecified() {
        val toBeTested = Solution.yes(aQuery)

        assertEquals(Substitution.empty(), toBeTested.substitution)
    }

    @Test
    fun yesSolutionComputesSolvedQueryApplyingSubstitutionToQuery() {
        val toBeTested = Solution.yes(aQuery, aSubstitution)

        assertEquals(theQuerySolved, toBeTested.solvedQuery)
    }

    @Test
    fun yesSolutionSecondaryConstructorWorksAsExpected() {
        val toBeTested = Solution.yes(querySignature, queryArgList, aSubstitution)

        assertEquals(aQuery, toBeTested.query)
        assertEquals(aSubstitution, toBeTested.substitution)
        assertEquals(theQuerySolved, toBeTested.solvedQuery)
    }

    @Test
    fun yesSolutionSecondaryConstructorComplainsIfArityDoesntMatchArgumentsCount() {
        assertFailsWith<IllegalArgumentException> { Solution.yes(querySignature, emptyList(), Substitution.empty()) }
        assertFailsWith<IllegalArgumentException> {
            Solution.yes(querySignature, listOf(Truth.TRUE, Truth.TRUE), Substitution.empty())
        }
    }

    @Test
    fun yesSolutionSecondaryConstructorComplainsIfSignatureVararg() {
        assertFailsWith<IllegalArgumentException> {
            Solution.yes(querySignature.copy(vararg = true), queryArgList, Substitution.empty())
        }
    }

    @Test
    fun noSolutionContainsInsertedData() {
        assertEquals(aQuery, Solution.no(aQuery).query)
    }

    @Test
    fun noSolutionSolutionAlwaysNull() {
        assertNull(Solution.no(aQuery).solvedQuery)
    }

    @Test
    fun noSolutionSubstitutionAlwaysFailed() {
        assertEquals(Substitution.failed(), Solution.no(aQuery).substitution)
    }

    @Test
    fun noSolutionSecondaryConstructorWorksAsExpected() {
        val toBeTested = Solution.no(querySignature, queryArgList)

        assertEquals(aQuery, toBeTested.query)
    }

    @Test
    fun noSolutionSecondaryConstructorComplainsIfArityDoesntMatchArgumentsCount() {
        assertFailsWith<IllegalArgumentException> { Solution.no(querySignature, emptyList()) }
        assertFailsWith<IllegalArgumentException> {
            Solution.no(querySignature, listOf(Truth.TRUE, Truth.TRUE))
        }
    }

    @Test
    fun noSolutionSecondaryConstructorComplainsIfSignatureVararg() {
        assertFailsWith<IllegalArgumentException> {
            Solution.no(querySignature.copy(vararg = true), queryArgList)
        }
    }

    @Test
    fun haltSolutionContainsInsertedData() {
        val toBeTested = Solution.halt(aQuery, anException)

        assertEquals(aQuery, toBeTested.query)
        assertEquals(anException, toBeTested.exception)
    }

    @Test
    fun haltSolutionSolutionAlwaysNull() {
        assertNull(Solution.halt(aQuery, anException).solvedQuery)
    }

    @Test
    fun haltSolutionSubstitutionAlwaysFailed() {
        assertEquals(Substitution.failed(), Solution.halt(aQuery, anException).substitution)
    }

    @Test
    fun haltSolutionSecondaryConstructorWorksAsExpected() {
        val toBeTested = Solution.halt(querySignature, queryArgList, anException)

        assertEquals(aQuery, toBeTested.query)
    }

    @Test
    fun haltSolutionSecondaryConstructorComplainsIfArityDoesntMatchArgumentsCount() {
        assertFailsWith<IllegalArgumentException> { Solution.halt(querySignature, emptyList(), anException) }
        assertFailsWith<IllegalArgumentException> {
            Solution.halt(querySignature, listOf(Truth.TRUE, Truth.TRUE), anException)
        }
    }

    @Test
    fun haltSolutionSecondaryConstructorComplainsIfSignatureVararg() {
        assertFailsWith<IllegalArgumentException> {
            Solution.halt(querySignature.copy(vararg = true), queryArgList, anException)
        }
    }
}
