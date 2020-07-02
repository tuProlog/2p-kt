package it.unibo.tuprolog.solve.stdlib.primitive.testutils

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.dsl.prolog
import it.unibo.tuprolog.solve.exception.error.EvaluationError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.stdlib.primitive.*
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.PrimitiveUtils.createSolveRequest

/**
 * Utils singleton to help testing arithmetic primitives
 *
 * @author Enrico
 */
internal object ArithmeticUtils {

    /** [Is] primitive test data (input, [Substitution | ErrorType]) */
    internal val isQueryToResult by lazy {
        prolog {
            mapOf(
                Is.functor("Y", "*"("+"(1, 2), 3)) to ("Y" to 9),
                Is.functor("Result", "+"(3, 11.0)) to ("Result" to 14.0),
                Is.functor("foo", 77) to Substitution.failed(),
                Is.functor(numOf(1.0), numOf(1)) to Substitution.failed(),
                Is.functor("X", "+"("+"("N", 1), "/"(3, 0))) to InstantiationError::class,
                Is.functor("C", "/"(3, 0)) to EvaluationError::class
            ).mapKeys { (query, _) -> createSolveRequest(query) }
        }
    }

    /** Equal functor test data (input, [true | false | ErrorType]) */
    internal val equalQueryToResult by lazy {
        prolog {
            mapOf(
                ArithmeticEqual.functor(1.0, 1) to true,
                ArithmeticEqual.functor("*"(3, 2), "-"(7, 1)) to true,
                ArithmeticEqual.functor(0.333, "/"(1, 3)) to false,
                ArithmeticEqual.functor(0, 1) to false,
                ArithmeticEqual.functor(1, "+"("N", "/"(3, 0))) to InstantiationError::class
            ).mapKeys { (query, _) -> createSolveRequest(query) }
        }
    }

    /** NotEqual functor test data (input, [true | false | ErrorType]) */
    internal val notEqualQueryToResult by lazy {
        prolog {
            mapOf(
                ArithmeticNotEqual.functor(0, 1) to true,
                ArithmeticNotEqual.functor(0.333, "/"(1, 3)) to true,
                ArithmeticNotEqual.functor(1.0, 1) to false,
                ArithmeticNotEqual.functor("*"(3, 2), "-"(7, 1)) to false,
                ArithmeticNotEqual.functor(1, "+"("N", "/"(3, 0))) to InstantiationError::class
            ).mapKeys { (query, _) -> createSolveRequest(query) }
        }
    }

    /** Greater functor test data (input, [true | false | ErrorType]) */
    internal val greaterQueryToResult by lazy {
        prolog {
            mapOf(
                ArithmeticGreaterThan.functor("*"(3, 2), "-"(6, 1)) to true,
                ArithmeticGreaterThan.functor(1.0, 1) to false,
                ArithmeticGreaterThan.functor(0, 1) to false,
                ArithmeticGreaterThan.functor(0.333, "/"(1, 3)) to false,
                ArithmeticGreaterThan.functor("X", 5) to InstantiationError::class,
                ArithmeticGreaterThan.functor("N", "/"(3, 0)) to InstantiationError::class
            ).mapKeys { (query, _) -> createSolveRequest(query) }
        }
    }

    /** Greater functor test data (input, [true | false | ErrorType]) */
    internal val greaterOrEqualQueryToResult by lazy {
        prolog {
            mapOf(
                ArithmeticGreaterThanOrEqualTo.functor("*"(3, 2), "-"(7, 1)) to true,
                ArithmeticGreaterThanOrEqualTo.functor(1.0, 1) to true,
                ArithmeticGreaterThanOrEqualTo.functor(0, 1) to false,
                ArithmeticGreaterThanOrEqualTo.functor(0.333, "/"(1, 3)) to false,
                ArithmeticGreaterThanOrEqualTo.functor("X", 5) to InstantiationError::class,
                ArithmeticGreaterThanOrEqualTo.functor("N", "/"(3, 0)) to InstantiationError::class
            ).mapKeys { (query, _) -> createSolveRequest(query) }
        }
    }

    /** Greater functor test data (input, [true | false | ErrorType]) */
    internal val lowerQueryToResult by lazy {
        prolog {
            mapOf(
                ArithmeticLowerThan.functor(0, 1) to true,
                ArithmeticLowerThan.functor(0.333, "/"(1, 3)) to true,
                ArithmeticLowerThan.functor(1.0, 1) to false,
                ArithmeticLowerThan.functor("*"(3, 2), "-"(7, 1)) to false,
                ArithmeticLowerThan.functor("X", 5) to InstantiationError::class,
                ArithmeticLowerThan.functor(1, "+"("N", "/"(3, 0))) to InstantiationError::class
            ).mapKeys { (query, _) -> createSolveRequest(query) }
        }
    }

    /** Greater functor test data (input, [true | false | ErrorType]) */
    internal val lowerOrEqualQueryToResult by lazy {
        prolog {
            mapOf(
                ArithmeticLowerThanOrEqualTo.functor(0, 1) to true,
                ArithmeticLowerThanOrEqualTo.functor(1.0, 1) to true,
                ArithmeticLowerThanOrEqualTo.functor(0.333, "/"(1, 3)) to true,
                ArithmeticLowerThanOrEqualTo.functor("*"(3, 2), "-"(6, 1)) to false,
                ArithmeticLowerThanOrEqualTo.functor("X", 5) to InstantiationError::class,
                ArithmeticLowerThanOrEqualTo.functor("N", "/"(3, 0)) to InstantiationError::class
            ).mapKeys { (query, _) -> createSolveRequest(query) }
        }
    }

}
