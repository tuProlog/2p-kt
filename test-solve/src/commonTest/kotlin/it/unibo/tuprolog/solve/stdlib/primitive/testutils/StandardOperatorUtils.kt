package it.unibo.tuprolog.solve.stdlib.primitive.testutils

import it.unibo.tuprolog.dsl.prolog
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.solve.stdlib.primitive.*
import kotlin.reflect.KClass
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.test.fail

internal object StandardOperatorUtils {

    internal fun assertCorrectResponse(
        standardOrderRelation: StandardOrderRelation<out ExecutionContext>,
        input: Solve.Request<ExecutionContext>,
        expectedResult: Any
    ) = when (expectedResult) {
        true -> assertTrue("Requesting ${input.query} should result in $expectedResult response!") {
            standardOrderRelation.wrappedImplementation(input).single().solution is Solution.Yes
        }
        false -> assertTrue("Requesting ${input.query} should result in $expectedResult response!") {
            standardOrderRelation.wrappedImplementation(input).single().solution is Solution.No
        }
        else ->
            @Suppress("UNCHECKED_CAST")
            (expectedResult as? KClass<out TuPrologRuntimeException>)
                ?.let { assertFailsWith(expectedResult) { standardOrderRelation.wrappedImplementation(input) } }
                ?: fail("Bad written test data!")
    }

    /** =@= test */
    internal val standardOrderEqualTest by lazy {
        prolog {
            mapOf(
                StandardOrderEqual.functor(1.0, 1.0) to true,
                StandardOrderEqual.functor(1.0, 1) to false,
                StandardOrderEqual.functor("stringTest", "stringTest") to true,
                StandardOrderEqual.functor("stringTest", 1.0) to false
            ).mapKeys { (query, _) -> PrimitiveUtils.createSolveRequest(query) }
        }
    }

    /** @> test */
    internal val standardOrderGreaterThanTest by lazy {
        prolog {
            mapOf(
                StandardOrderGreaterThan.functor(1, 1.0) to true,
                StandardOrderGreaterThan.functor(1.0, 1) to false,
                StandardOrderGreaterThan.functor("stringTest", 1) to true,
                StandardOrderGreaterThan.functor("stringTesta", "stringTestb") to false,
                StandardOrderGreaterThan.functor("stringTestb", "stringTesta") to true
            ).mapKeys { (query, _) -> PrimitiveUtils.createSolveRequest(query) }
        }
    }

    /** @>= test */
    internal val standardOrderGreaterThanOrEqualToTest by lazy {
        prolog {
            mapOf(
                StandardOrderGreaterThanOrEqualTo.functor(1, 1) to true,
                StandardOrderGreaterThanOrEqualTo.functor(1.0, 1) to false,
                StandardOrderGreaterThanOrEqualTo.functor("stringTest", "stringTest") to true,
                StandardOrderGreaterThanOrEqualTo.functor("stringTest", "stringTest1") to false,
                StandardOrderGreaterThanOrEqualTo.functor("stringTest", 1) to true
            ).mapKeys { (query, _) -> PrimitiveUtils.createSolveRequest(query) }
        }
    }

    /** @< test */
    internal val standardOrderLowerThanTest by lazy {
        prolog {
            mapOf(
                StandardOrderLowerThan.functor(1.0, 1) to true,
                StandardOrderLowerThan.functor(1, 1.0) to false,
                StandardOrderLowerThan.functor("stringTestA", "stringTestZ") to true,
                StandardOrderLowerThan.functor(1.0, "stringTest") to true
            ).mapKeys { (query, _) -> PrimitiveUtils.createSolveRequest(query) }
        }
    }

    /** @< test */
    internal val standardOrderLowerThanOrEqualToTest by lazy {
        prolog {
            mapOf(  StandardOrderLowerThanOrEqualTo.functor(1, 1.0) to false,
                StandardOrderLowerThanOrEqualTo.functor(1.0, 1.0) to true,
                StandardOrderLowerThanOrEqualTo.functor("stringTest", "stringTest") to true
            ).mapKeys { (query, _) -> PrimitiveUtils.createSolveRequest(query) }
        }
    }
}