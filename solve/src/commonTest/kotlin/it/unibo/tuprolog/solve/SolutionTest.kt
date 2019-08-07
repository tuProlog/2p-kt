package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Truth
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [Solution], [Solution.Yes] and [Solution.No]
 *
 * @author Enrico
 */
internal class SolutionTest {

    private val aQuery = Truth.`true`()
    private val aSolution = Truth.`true`()
    private val aSubstitution = Substitution.empty()

    @Test
    fun yesSolutionContainsInsertedData() {
        val toBeTested = Solution.Yes(aQuery, aSolution, aSubstitution)

        assertEquals(aQuery, toBeTested.query)
        assertEquals(aSolution, toBeTested.solution)
        assertEquals(aSubstitution, toBeTested.substitution)
    }

    @Test
    fun noSolutionContainsInsertedData() {
        assertEquals(aQuery, Solution.No(aQuery).query)
    }

    @Test
    fun noSolutionSolutionAlwaysNull() {
        assertEquals(null, Solution.No(aQuery).solution)
    }

    @Test
    fun noSolutionSubstitutionAlwaysFailed() {
        assertEquals(Substitution.failed(), Solution.No(aQuery).substitution)
    }

}
