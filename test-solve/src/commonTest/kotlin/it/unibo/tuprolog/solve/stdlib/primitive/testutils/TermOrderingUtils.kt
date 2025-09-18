package it.unibo.tuprolog.solve.stdlib.primitive.testutils

import it.unibo.tuprolog.dsl.logicProgramming
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.stdlib.primitive.TermGreaterThan
import it.unibo.tuprolog.solve.stdlib.primitive.TermGreaterThanOrEqualTo
import it.unibo.tuprolog.solve.stdlib.primitive.TermLowerThan
import it.unibo.tuprolog.solve.stdlib.primitive.TermLowerThanOrEqualTo
import it.unibo.tuprolog.solve.stdlib.primitive.TermNotSame
import it.unibo.tuprolog.solve.stdlib.primitive.TermSame
import kotlin.reflect.KClass
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.test.fail

internal object TermOrderingUtils {
    @Suppress("IMPLICIT_CAST_TO_ANY")
    internal fun assertCorrectResponse(
        standardOrderRelation: BinaryRelation.Predicative<out ExecutionContext>,
        input: Solve.Request<ExecutionContext>,
        expectedResult: Any,
    ) = when (expectedResult) {
        true ->
            assertTrue("Requesting ${input.query} should result in $expectedResult response!") {
                standardOrderRelation.implementation
                    .solve(input)
                    .single()
                    .solution is Solution.Yes
            }
        false ->
            assertTrue("Requesting ${input.query} should result in $expectedResult response!") {
                standardOrderRelation.implementation
                    .solve(input)
                    .single()
                    .solution is Solution.No
            }
        else ->
            @Suppress("UNCHECKED_CAST")
            (expectedResult as? KClass<out ResolutionException>)
                ?.let { assertFailsWith(expectedResult) { standardOrderRelation.implementation.solve(input) } }
                ?: fail("Bad written test data!")
    }

    /** =@= test */
    internal val standardOrderEqualTest by lazy {
        logicProgramming {
            mapOf(
                TermSame.functor(realOf(1.0), realOf(1.0)) to true,
                TermSame.functor("stringTest", "stringTest") to true,
                TermSame.functor("stringTest", realOf(1.0)) to false,
            ).mapKeys { (query, _) -> PrimitiveUtils.createSolveRequest(query) }
        }
    }

    /** \=@= test */
    internal val standardOrderNotEqualTest by lazy {
        logicProgramming {
            mapOf(
                TermNotSame.functor(realOf(1.0), realOf(1.0)) to false,
                TermNotSame.functor("stringTest", "stringTest") to false,
                TermNotSame.functor("stringTest", realOf(1.0)) to true,
            ).mapKeys { (query, _) -> PrimitiveUtils.createSolveRequest(query) }
        }
    }

    /** @> test */
    internal val standardOrderGreaterThanTest by lazy {
        logicProgramming {
            mapOf(
                TermGreaterThan.functor(intOf(1), realOf(1.0)) to true,
                TermGreaterThan.functor(realOf(1.0), intOf(1)) to false,
                TermGreaterThan.functor("stringTest", intOf(1)) to true,
                TermGreaterThan.functor("stringTesta", "stringTestb") to false,
                TermGreaterThan.functor("stringTestb", "stringTesta") to true,
            ).mapKeys { (query, _) -> PrimitiveUtils.createSolveRequest(query) }
        }
    }

    /** @>= test */
    internal val standardOrderGreaterThanOrEqualToTest by lazy {
        logicProgramming {
            mapOf(
                TermGreaterThanOrEqualTo.functor(intOf(1), intOf(1)) to true,
                TermGreaterThanOrEqualTo.functor("stringTest", "stringTest") to true,
                TermGreaterThanOrEqualTo.functor("stringTest", "stringTest1") to false,
                TermGreaterThanOrEqualTo.functor("stringTest", intOf(1)) to true,
            ).mapKeys { (query, _) -> PrimitiveUtils.createSolveRequest(query) }
        }
    }

    /** @< test */
    internal val standardOrderLowerThanTest by lazy {
        logicProgramming {
            mapOf(
                TermLowerThan.functor(realOf(1.0), intOf(1)) to true,
                TermLowerThan.functor(intOf(1), realOf(1.0)) to false,
                TermLowerThan.functor("stringTestA", "stringTestZ") to true,
                TermLowerThan.functor(realOf(1.0), "stringTest") to true,
            ).mapKeys { (query, _) -> PrimitiveUtils.createSolveRequest(query) }
        }
    }

    /** @<= test */
    internal val standardOrderLowerThanOrEqualToTest by lazy {
        logicProgramming {
            mapOf(
                TermLowerThanOrEqualTo.functor(intOf(1), realOf(1.0)) to false,
                TermLowerThanOrEqualTo.functor(realOf(1.0), realOf(1.0)) to true,
                TermLowerThanOrEqualTo.functor("stringTest", "stringTest") to true,
            ).mapKeys { (query, _) -> PrimitiveUtils.createSolveRequest(query) }
        }
    }
}
