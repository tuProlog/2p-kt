package it.unibo.tuprolog.solve.stdlib.primitive.testutils

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.exception.PrologError
import kotlin.reflect.KClass
import kotlin.test.assertEquals

/**
 * Utils singleton to help testing Primitive implementations
 *
 * @author Enrico
 */
internal object PrimitiveUtils {

    /** Utility function to assert that there's only one Solution of given type, with given query and substitution */
    internal fun assertOnlyOneSolution(expectedSolution: Solution, solutions: Sequence<Solve.Response>) {
        assertEquals(1, solutions.count(), "Expected only one solution, but ${solutions.toList()}")
        with(solutions.single().solution) {
            assertEquals(expectedSolution::class, this::class)
            assertEquals(expectedSolution.query, query)
            assertEquals(expectedSolution.substitution, substitution)
        }
    }

    /** Utility function to extract deep cause of a [Solution.Halt] exception */
    internal fun Solution.Halt.deepCause(): Throwable {
        var exceptionCause: Throwable = exception

        while (exceptionCause.cause != null) {
            exceptionCause = exceptionCause.cause!!
        }

        return exceptionCause
    }

    /** Utility function to assert the [PrologError] type correctness of [Solution.Halt] */
    internal fun assertPrologError(expected: KClass<out PrologError>, actualSolution: Solution) =
        assertEquals(expected, (actualSolution as Solution.Halt).exception::class)
}
