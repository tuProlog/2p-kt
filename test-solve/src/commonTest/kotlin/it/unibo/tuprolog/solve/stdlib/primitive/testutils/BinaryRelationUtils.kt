package it.unibo.tuprolog.solve.stdlib.primitive.testutils

import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import kotlin.reflect.KClass

object BinaryRelationUtils {
    /** Utility method to check if the term relation responses are correct */
    @Suppress("IMPLICIT_CAST_TO_ANY")
    internal fun assertCorrectResponse(
        termRelation: BinaryRelation<out ExecutionContext>,
        input: Solve.Request<ExecutionContext>,
        expectedResult: Any,
    ) = when (expectedResult) {
        true ->
            withClue("Requesting ${input.query} should result in $expectedResult response!") {
                (
                    run {
                        termRelation.implementation
                            .solve(input)
                            .single()
                            .solution is Solution.Yes
                    }
                ).shouldBeTrue()
            }
        false ->
            withClue("Requesting ${input.query} should result in $expectedResult response!") {
                (
                    run {
                        termRelation.implementation
                            .solve(input)
                            .single()
                            .solution is Solution.No
                    }
                ).shouldBeTrue()
            }
        else ->
            @Suppress("UNCHECKED_CAST")
            (expectedResult as? KClass<out ResolutionException>)
                ?.let { shouldThrowAny { termRelation.implementation.solve(input) }.shouldBeInstanceOf(expectedResult) }
                ?: fail("Bad written test data!")
    }
}
