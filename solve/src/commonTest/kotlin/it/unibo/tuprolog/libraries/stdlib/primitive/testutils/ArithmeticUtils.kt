package it.unibo.tuprolog.libraries.stdlib.primitive.testutils

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.libraries.stdlib.CommonBuiltins
import it.unibo.tuprolog.libraries.stdlib.primitive.*
import it.unibo.tuprolog.primitive.toSignature
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.solve.exception.prologerror.EvaluationError
import it.unibo.tuprolog.solve.exception.prologerror.InstantiationError
import it.unibo.tuprolog.testutils.DummyInstances
import kotlin.reflect.KClass
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Utils singleton to help testing arithmetic primitives
 *
 * @author Enrico
 */
internal object ArithmeticUtils {

    private val commonBuiltinsContext = object : ExecutionContext by DummyInstances.executionContext {
        override val libraries: Libraries = Libraries(CommonBuiltins)
    }

    /** Creates a solve request with given query struct */
    private fun createPrimitiveRequest(query: Struct): Solve.Request<ExecutionContext> =
            Solve.Request<ExecutionContext>(
                    query.toSignature(),
                    query.argsList,
                    commonBuiltinsContext
            )

    /** Utility method to check if the arithmetic relation responses are correct */
    internal fun assertCorrectResponse(
            arithmeticRelation: ArithmeticRelation<out ExecutionContext>,
            input: Solve.Request<ExecutionContext>,
            expectedResult: Any
    ) = when (expectedResult) {
        true -> assertTrue("Requesting ${input.query} should result in $expectedResult response!") {
            arithmeticRelation.primitive(input).single().solution is Solution.Yes
        }
        false -> assertTrue("Requesting ${input.query} should result in $expectedResult response!") {
            arithmeticRelation.primitive(input).single().solution is Solution.No
        }
        else ->
            @Suppress("UNCHECKED_CAST")
            (expectedResult as? KClass<out TuPrologRuntimeException>)
                    ?.let { assertFailsWith(expectedResult) { arithmeticRelation.primitive(input) } }
                    ?: fail("Bad written test data!")
    }

    /** [Is] primitive test data (input, [Substitution | ErrorType]) */
    internal val isQueryToResult by lazy {
        Scope.empty {
            mapOf(
                    structOf(
                            Is.functor,
                            varOf("Y"),
                            structOf("*",
                                    structOf("+",
                                            numOf(1),
                                            numOf(2)
                                    ),
                                    numOf(3)
                            )
                    ) to Substitution.of(varOf("Y"), numOf(9)),
                    structOf(
                            Is.functor,
                            varOf("Result"),
                            structOf("+", numOf(3), numOf(11.0))
                    ) to Substitution.of(varOf("Result"), numOf(14.0)),
                    structOf(
                            Is.functor,
                            atomOf("foo"),
                            numOf(77)
                    ) to Substitution.failed(),
                    structOf(
                            Is.functor,
                            numOf(1.0),
                            numOf(1)
                    ) to Substitution.failed(),
                    structOf(
                            Is.functor,
                            varOf("X"),
                            structOf(
                                    "+",
                                    structOf("+", varOf("N"), numOf(1)),
                                    structOf("/", numOf(3), numOf(0))
                            )
                    ) to InstantiationError::class,
                    structOf(
                            Is.functor,
                            varOf("C"),
                            structOf("/", numOf(3), numOf(0))
                    ) to EvaluationError::class
            ).mapKeys { (query, _) -> createPrimitiveRequest(query) }
        }
    }

    /** Equal functor test data (input, [true | false | ErrorType]) */
    internal val equalQueryToResult by lazy {
        Scope.empty {
            mapOf(
                    structOf(
                            ArithmeticEqual.functor,
                            numOf(1.0),
                            numOf(1)
                    ) to true,
                    structOf(
                            ArithmeticEqual.functor,
                            structOf("*", numOf(3), numOf(2)),
                            structOf("-", numOf(7), numOf(1))
                    ) to true,
                    structOf(
                            ArithmeticEqual.functor,
                            numOf(0.333),
                            structOf("/", numOf(1), numOf(3))
                    ) to false,
                    structOf(
                            ArithmeticEqual.functor,
                            numOf(0),
                            numOf(1)
                    ) to false,
                    structOf(
                            ArithmeticEqual.functor,
                            numOf(1),
                            structOf(
                                    "+",
                                    varOf("N"),
                                    structOf("/", numOf(3), numOf(0))
                            )
                    ) to InstantiationError::class
            ).mapKeys { (query, _) -> createPrimitiveRequest(query) }
        }
    }

    /** NotEqual functor test data (input, [true | false | ErrorType]) */
    internal val notEqualQueryToResult by lazy {
        Scope.empty {
            mapOf(
                    structOf(
                            ArithmeticNotEqual.functor,
                            numOf(0),
                            numOf(1)
                    ) to true,
                    structOf(
                            ArithmeticNotEqual.functor,
                            numOf(0.333),
                            structOf("/", numOf(1), numOf(3))
                    ) to true,
                    structOf(
                            ArithmeticNotEqual.functor,
                            numOf(1.0),
                            numOf(1)
                    ) to false,
                    structOf(
                            ArithmeticNotEqual.functor,
                            structOf("*", numOf(3), numOf(2)),
                            structOf("-", numOf(7), numOf(1))
                    ) to false,
                    structOf(
                            ArithmeticNotEqual.functor,
                            numOf(1),
                            structOf(
                                    "+",
                                    varOf("N"),
                                    structOf("/", numOf(3), numOf(0))
                            )
                    ) to InstantiationError::class
            ).mapKeys { (query, _) -> createPrimitiveRequest(query) }
        }
    }

    /** Greater functor test data (input, [true | false | ErrorType]) */
    internal val greaterQueryToResult by lazy {
        Scope.empty {
            mapOf(
                    structOf(
                            ArithmeticGreaterThan.functor,
                            structOf("*", numOf(3), numOf(2)),
                            structOf("-", numOf(6), numOf(1))
                    ) to true,
                    structOf(
                            ArithmeticGreaterThan.functor,
                            numOf(1.0),
                            numOf(1)
                    ) to false,
                    structOf(
                            ArithmeticGreaterThan.functor,
                            numOf(0),
                            numOf(1)
                    ) to false,
                    structOf(
                            ArithmeticGreaterThan.functor,
                            numOf(0.333),
                            structOf("/", numOf(1), numOf(3))
                    ) to false,
                    structOf(
                            ArithmeticGreaterThan.functor,
                            varOf("X"),
                            numOf(5)
                    ) to InstantiationError::class,
                    structOf(
                            ArithmeticGreaterThan.functor,
                            varOf("N"),
                            structOf("/", numOf(3), numOf(0))
                    ) to InstantiationError::class
            ).mapKeys { (query, _) -> createPrimitiveRequest(query) }
        }
    }

    /** Greater functor test data (input, [true | false | ErrorType]) */
    internal val greaterOrEqualQueryToResult by lazy {
        Scope.empty {
            mapOf(
                    structOf(
                            ArithmeticGreaterThanOrEqualTo.functor,
                            structOf("*", numOf(3), numOf(2)),
                            structOf("-", numOf(7), numOf(1))
                    ) to true,
                    structOf(
                            ArithmeticGreaterThanOrEqualTo.functor,
                            numOf(1.0),
                            numOf(1)
                    ) to true,
                    structOf(
                            ArithmeticGreaterThanOrEqualTo.functor,
                            numOf(0),
                            numOf(1)
                    ) to false,
                    structOf(
                            ArithmeticGreaterThanOrEqualTo.functor,
                            numOf(0.333),
                            structOf("/", numOf(1), numOf(3))
                    ) to false,
                    structOf(
                            ArithmeticGreaterThanOrEqualTo.functor,
                            varOf("X"),
                            numOf(5)
                    ) to InstantiationError::class,
                    structOf(
                            ArithmeticGreaterThanOrEqualTo.functor,
                            varOf("N"),
                            structOf("/", numOf(3), numOf(0))
                    ) to InstantiationError::class
            ).mapKeys { (query, _) -> createPrimitiveRequest(query) }
        }
    }

    /** Greater functor test data (input, [true | false | ErrorType]) */
    internal val lowerQueryToResult by lazy {
        Scope.empty {
            mapOf(
                    structOf(
                            ArithmeticLowerThan.functor,
                            numOf(0),
                            numOf(1)
                    ) to true,
                    structOf(
                            ArithmeticLowerThan.functor,
                            numOf(0.333),
                            structOf("/", numOf(1), numOf(3))
                    ) to true,
                    structOf(
                            ArithmeticLowerThan.functor,
                            numOf(1.0),
                            numOf(1)
                    ) to false,
                    structOf(
                            ArithmeticLowerThan.functor,
                            structOf("*", numOf(3), numOf(2)),
                            structOf("-", numOf(7), numOf(1))
                    ) to false,
                    structOf(
                            ArithmeticLowerThan.functor,
                            varOf("X"),
                            numOf(5)
                    ) to InstantiationError::class,
                    structOf(
                            ArithmeticLowerThan.functor,
                            numOf(1),
                            structOf(
                                    "+",
                                    varOf("N"),
                                    structOf("/", numOf(3), numOf(0))
                            )
                    ) to InstantiationError::class
            ).mapKeys { (query, _) -> createPrimitiveRequest(query) }
        }
    }

    /** Greater functor test data (input, [true | false | ErrorType]) */
    internal val lowerOrEqualQueryToResult by lazy {
        Scope.empty {
            mapOf(
                    structOf(
                            ArithmeticLowerThanOrEqualTo.functor,
                            numOf(0),
                            numOf(1)
                    ) to true,
                    structOf(
                            ArithmeticLowerThanOrEqualTo.functor,
                            numOf(1.0),
                            numOf(1)
                    ) to true,
                    structOf(
                            ArithmeticLowerThanOrEqualTo.functor,
                            numOf(0.333),
                            structOf("/", numOf(1), numOf(3))
                    ) to true,
                    structOf(
                            ArithmeticLowerThanOrEqualTo.functor,
                            structOf("*", numOf(3), numOf(2)),
                            structOf("-", numOf(6), numOf(1))
                    ) to false,
                    structOf(
                            ArithmeticLowerThanOrEqualTo.functor,
                            varOf("X"),
                            numOf(5)
                    ) to InstantiationError::class,
                    structOf(
                            ArithmeticLowerThanOrEqualTo.functor,
                            varOf("N"),
                            structOf("/", numOf(3), numOf(0))
                    ) to InstantiationError::class
            ).mapKeys { (query, _) -> createPrimitiveRequest(query) }
        }
    }

}
