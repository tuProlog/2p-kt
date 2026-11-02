package it.unibo.tuprolog.solve.stdlib.primitive

import io.kotest.assertions.fail
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.ArithmeticUtils.isQueryToResult
import kotlin.reflect.KClass

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
                        e::class shouldBe expectedResult
                    }
                }
                is Substitution.Unifier -> {
                    Is.implementation.solve(input).single().solution.let {
                        withClue("Requesting ${input.query} should result in $expectedResult response!") {
                            it.shouldBeInstanceOf<Solution.Yes>()
                        }
                        it.substitution shouldBe expectedResult
                    }
                }
                is Substitution.Fail -> {
                    Is.implementation.solve(input).single().solution.let {
                        withClue("Requesting ${input.query} should result in $expectedResult response!") {
                            it.shouldBeInstanceOf<Solution.No>()
                        }
                    }
                }
                else -> fail("Bad written test data!")
            }
        }
    })
