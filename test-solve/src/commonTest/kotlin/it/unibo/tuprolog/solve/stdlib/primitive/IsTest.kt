package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.ArithmeticUtils.isQueryToResult
import kotlin.reflect.KClass
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Test class for [Is]
 *
 * @author Enrico
 */
internal class IsTest {

    @Test
    fun computesCorrectly() {
        isQueryToResult.forEach { (input, expectedResult) ->
            when (expectedResult) {
                is KClass<*> -> {
                    try {
                        Is.implementation.solve(input)
                        fail("Expected: $expectedResult but no exception was thrown")
                    } catch (e: ResolutionException) {
                        assertEquals(expectedResult, e::class)
                    }
                }
                is Substitution.Unifier -> {
                    Is.implementation.solve(input).single().solution.let {
                        assertTrue("Requesting ${input.query} should result in $expectedResult response!") {
                            it is Solution.Yes
                        }
                        assertEquals(expectedResult, it.substitution)
                    }
                }
                is Substitution.Fail -> {
                    Is.implementation.solve(input).single().solution.let {
                        assertTrue("Requesting ${input.query} should result in $expectedResult response!") {
                            it is Solution.No
                        }
                    }
                }
            }
        }
    }
}
