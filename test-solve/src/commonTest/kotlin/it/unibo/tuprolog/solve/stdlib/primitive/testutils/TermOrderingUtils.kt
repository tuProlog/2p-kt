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
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.PrimitiveUtils.requestCase
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
            listOf(
                requestCase("same-real", TermSame.functor(realOf(1.0), realOf(1.0)), true),
                requestCase("same-atom", TermSame.functor("stringTest", "stringTest"), true),
                requestCase("atom-vs-real", TermSame.functor("stringTest", realOf(1.0)), false),
            )
        }
    }

    /** \=@= test */
    internal val standardOrderNotEqualTest by lazy {
        logicProgramming {
            listOf(
                requestCase("same-real", TermNotSame.functor(realOf(1.0), realOf(1.0)), false),
                requestCase("same-atom", TermNotSame.functor("stringTest", "stringTest"), false),
                requestCase("atom-vs-real", TermNotSame.functor("stringTest", realOf(1.0)), true),
            )
        }
    }

    /** @> test */
    internal val standardOrderGreaterThanTest by lazy {
        logicProgramming {
            listOf(
                requestCase("integer-vs-real", TermGreaterThan.functor(intOf(1), realOf(1.0)), true),
                requestCase("real-vs-integer", TermGreaterThan.functor(realOf(1.0), intOf(1)), false),
                requestCase("atom-vs-integer", TermGreaterThan.functor("stringTest", intOf(1)), true),
                requestCase("lower-atom", TermGreaterThan.functor("stringTesta", "stringTestb"), false),
                requestCase("greater-atom", TermGreaterThan.functor("stringTestb", "stringTesta"), true),
            )
        }
    }

    /** @>= test */
    internal val standardOrderGreaterThanOrEqualToTest by lazy {
        logicProgramming {
            listOf(
                requestCase("same-integer", TermGreaterThanOrEqualTo.functor(intOf(1), intOf(1)), true),
                requestCase("same-atom", TermGreaterThanOrEqualTo.functor("stringTest", "stringTest"), true),
                requestCase("lower-atom", TermGreaterThanOrEqualTo.functor("stringTest", "stringTest1"), false),
                requestCase("atom-vs-integer", TermGreaterThanOrEqualTo.functor("stringTest", intOf(1)), true),
            )
        }
    }

    /** @< test */
    internal val standardOrderLowerThanTest by lazy {
        logicProgramming {
            listOf(
                requestCase("real-vs-integer", TermLowerThan.functor(realOf(1.0), intOf(1)), true),
                requestCase("integer-vs-real", TermLowerThan.functor(intOf(1), realOf(1.0)), false),
                requestCase("lower-atom", TermLowerThan.functor("stringTestA", "stringTestZ"), true),
                requestCase("real-vs-atom", TermLowerThan.functor(realOf(1.0), "stringTest"), true),
            )
        }
    }

    /** @<= test */
    internal val standardOrderLowerThanOrEqualToTest by lazy {
        logicProgramming {
            listOf(
                requestCase("integer-vs-real", TermLowerThanOrEqualTo.functor(intOf(1), realOf(1.0)), false),
                requestCase("same-real", TermLowerThanOrEqualTo.functor(realOf(1.0), realOf(1.0)), true),
                requestCase("same-atom", TermLowerThanOrEqualTo.functor("stringTest", "stringTest"), true),
            )
        }
    }
}
