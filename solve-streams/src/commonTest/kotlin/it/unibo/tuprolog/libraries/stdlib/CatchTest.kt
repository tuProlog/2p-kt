package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.libraries.stdlib.testutils.CatchUtils
import it.unibo.tuprolog.libraries.stdlib.testutils.PrimitivesUtils.assertErrorCauseChainComputedCorrectly
import it.unibo.tuprolog.libraries.stdlib.testutils.PrimitivesUtils.filterInterestingVariables
import it.unibo.tuprolog.solve.solver.testutils.SolverSLDUtils.assertSolutionsCorrect
import kotlin.test.Test

/**
 * Test class for [Catch]
 *
 * @author Enrico
 */
internal class CatchTest {

    @Test
    fun catchWorksLikeCallIfNoErrorIsThrownByFirstArgument() {
        CatchUtils.requestSolutionMap.forEach { (request, solutionList) ->
            val toBeTested = Catch.primitive(request).toList()

            assertSolutionsCorrect(solutionList, toBeTested.map { it.solution })
        }
    }

    @Test
    fun catchPrologStandardExamplesWorkAsExpected() {
        CatchUtils.prologStandardCatchExamples.forEach { (request, solutionList) ->
            val toBeTested = Catch.primitive(request).toList()

            assertSolutionsCorrect(solutionList, toBeTested.map { filterInterestingVariables(it.solution) })
        }
    }

    @Test
    fun catchThrowPrologStandardExamplesWorkAsExpected() {
        CatchUtils.prologStandardThrowExamples.forEach { (request, solutionList) ->
            val toBeTested = Catch.primitive(request).toList()

            assertSolutionsCorrect(solutionList, toBeTested.map { filterInterestingVariables(it.solution) })
        }
    }

    @Test
    fun catchErrorChainComputedCorrectly() {
        CatchUtils.prologStandardThrowExamplesWithError.forEach { (request, solutionList) ->
            assertErrorCauseChainComputedCorrectly(request, solutionList.single())
        }
    }
}
