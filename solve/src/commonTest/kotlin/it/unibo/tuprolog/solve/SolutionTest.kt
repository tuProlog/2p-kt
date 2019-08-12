package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [Solution], [Solution.Yes] and [Solution.No]
 *
 * @author Enrico
 */
internal class SolutionTest {

    private val myScope = Scope.empty()
    private val aQuery = with(myScope) { Struct.of("f", varOf("A")) }
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
    fun noSolutionContainsInsertedData() {
        assertEquals(aQuery, Solution.No(aQuery).query)
    }

    @Test
    fun noSolutionSolutionAlwaysNull() {
        assertEquals(null, Solution.No(aQuery).solvedQuery)
    }

    @Test
    fun noSolutionSubstitutionAlwaysFailed() {
        assertEquals(Substitution.failed(), Solution.No(aQuery).substitution)
    }

}
