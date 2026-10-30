package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.ArithmeticUtils.isQueryToResult
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Test class for [Is]
 *
 * @author Enrico
 */
class IsSpec :
    FunSpec({
        withTests(nameFn = { "computes-correctly/${it.id}" }, isQueryToResult) { case ->
            val input = case.request
            when (val expectedResult = case.expected) {
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
                else -> fail("Bad written test data!")
            }
        }
    })
