package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.ArithmeticUtils.isQueryToResult
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import kotlin.reflect.KClass
import kotlin.test.*

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
                is Substitution.Unifier -> {
                    Is.wrappedImplementation(input).single().solution.let {
                        assertTrue("Requesting ${input.query} should result in $expectedResult response!") {
                            it is Solution.Yes
                        }
                        assertEquals(expectedResult, it.substitution)
                    }
                }
                is Substitution.Fail -> {
                    Is.wrappedImplementation(input).single().solution.let {
                        assertTrue("Requesting ${input.query} should result in $expectedResult response!") {
                            it is Solution.No
                        }
                    }
                }
                else ->
                    @Suppress("UNCHECKED_CAST")
                    (expectedResult as? KClass<out TuPrologRuntimeException>)
                        ?.let { assertFailsWith(expectedResult) { Is.wrappedImplementation(input) } }
                        ?: fail("Bad written test data!")
            }
        }
    }

}
