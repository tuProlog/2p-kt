package it.unibo.tuprolog.solve.stdlib.primitive.testutils

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import kotlin.reflect.KClass
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.test.fail

object BinaryRelationUtils {
    /** Utility method to check if the term relation responses are correct */
    @Suppress("IMPLICIT_CAST_TO_ANY")
    internal fun assertCorrectResponse(
        termRelation: BinaryRelation<out ExecutionContext>,
        input: Solve.Request<ExecutionContext>,
        expectedResult: Any,
    ) = when (expectedResult) {
        true ->
            assertTrue("Requesting ${input.query} should result in $expectedResult response!") {
                termRelation.implementation
                    .solve(input)
                    .single()
                    .solution is Solution.Yes
            }
        false ->
            assertTrue("Requesting ${input.query} should result in $expectedResult response!") {
                termRelation.implementation
                    .solve(input)
                    .single()
                    .solution is Solution.No
            }
        else ->
            @Suppress("UNCHECKED_CAST")
            (expectedResult as? KClass<out ResolutionException>)
                ?.let { assertFailsWith(expectedResult) { termRelation.implementation.solve(input) } }
                ?: fail("Bad written test data!")
    }
}
