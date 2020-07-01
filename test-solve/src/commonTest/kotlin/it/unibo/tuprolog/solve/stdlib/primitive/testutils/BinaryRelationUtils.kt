package it.unibo.tuprolog.solve.stdlib.primitive.testutils

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.solve.primitive.BinaryRelation
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
        expectedResult: Any
    ) = when (expectedResult) {
        true -> assertTrue("Requesting ${input.query} should result in $expectedResult response!") {
            termRelation.wrappedImplementation(input).single().solution is Solution.Yes
        }
        false -> assertTrue("Requesting ${input.query} should result in $expectedResult response!") {
            termRelation.wrappedImplementation(input).single().solution is Solution.No
        }
        else ->
            @Suppress("UNCHECKED_CAST")
            (expectedResult as? KClass<out TuPrologRuntimeException>)
                ?.let { assertFailsWith(expectedResult) { termRelation.wrappedImplementation(input) } }
                ?: fail("Bad written test data!")
    }
}