package it.unibo.tuprolog.solve.stdlib.primitive.testutils

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.dsl.logicProgramming
import it.unibo.tuprolog.solve.exception.error.EvaluationError
import it.unibo.tuprolog.solve.exception.error.InstantiationError
import it.unibo.tuprolog.solve.stdlib.primitive.ArithmeticEqual
import it.unibo.tuprolog.solve.stdlib.primitive.ArithmeticGreaterThan
import it.unibo.tuprolog.solve.stdlib.primitive.ArithmeticGreaterThanOrEqualTo
import it.unibo.tuprolog.solve.stdlib.primitive.ArithmeticLowerThan
import it.unibo.tuprolog.solve.stdlib.primitive.ArithmeticLowerThanOrEqualTo
import it.unibo.tuprolog.solve.stdlib.primitive.ArithmeticNotEqual
import it.unibo.tuprolog.solve.stdlib.primitive.Is
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.PrimitiveUtils.requestCase

/**
 * Utils singleton to help testing arithmetic primitives
 *
 * @author Enrico
 */
internal object ArithmeticUtils {
    /** [Is] primitive test data (input, [Substitution | ErrorType]) */
    internal val isQueryToResult by lazy {
        logicProgramming {
            listOf(
                requestCase("integer-expression", Is.functor("Y", "*"("+"(1, 2), 3)), "Y" to 9),
                requestCase("mixed-expression", Is.functor("Result", "+"(3, 11.0)), "Result" to 14.0),
                requestCase("atom-lhs", Is.functor("foo", 77), Substitution.failed()),
                requestCase("real-vs-integer", Is.functor(numOf(1.0), numOf(1)), Substitution.failed()),
                requestCase(
                    "unbound-var-in-expression",
                    Is.functor("X", "+"("+"("N", 1), "/"(3, 0))),
                    InstantiationError::class,
                ),
                requestCase("division-by-zero", Is.functor("C", "/"(3, 0)), EvaluationError::class),
            )
        }
    }

    /** Equal functor test data (input, [true | false | ErrorType]) */
    internal val equalQueryToResult by lazy {
        logicProgramming {
            listOf(
                requestCase("real-vs-integer", ArithmeticEqual.functor(1.0, 1), true),
                requestCase("equal-expressions", ArithmeticEqual.functor("*"(3, 2), "-"(7, 1)), true),
                requestCase("approximated-third", ArithmeticEqual.functor(0.333, "/"(1, 3)), false),
                requestCase("zero-vs-one", ArithmeticEqual.functor(0, 1), false),
                requestCase(
                    "unbound-var",
                    ArithmeticEqual.functor(1, "+"("N", "/"(3, 0))),
                    InstantiationError::class,
                ),
            )
        }
    }

    /** NotEqual functor test data (input, [true | false | ErrorType]) */
    internal val notEqualQueryToResult by lazy {
        logicProgramming {
            listOf(
                requestCase("zero-vs-one", ArithmeticNotEqual.functor(0, 1), true),
                requestCase("approximated-third", ArithmeticNotEqual.functor(0.333, "/"(1, 3)), true),
                requestCase("real-vs-integer", ArithmeticNotEqual.functor(1.0, 1), false),
                requestCase("equal-expressions", ArithmeticNotEqual.functor("*"(3, 2), "-"(7, 1)), false),
                requestCase(
                    "unbound-var",
                    ArithmeticNotEqual.functor(1, "+"("N", "/"(3, 0))),
                    InstantiationError::class,
                ),
            )
        }
    }

    /** Greater functor test data (input, [true | false | ErrorType]) */
    internal val greaterQueryToResult by lazy {
        logicProgramming {
            listOf(
                requestCase("greater-expression", ArithmeticGreaterThan.functor("*"(3, 2), "-"(6, 1)), true),
                requestCase("real-vs-integer", ArithmeticGreaterThan.functor(1.0, 1), false),
                requestCase("zero-vs-one", ArithmeticGreaterThan.functor(0, 1), false),
                requestCase("approximated-third", ArithmeticGreaterThan.functor(0.333, "/"(1, 3)), false),
                requestCase("unbound-var", ArithmeticGreaterThan.functor("X", 5), InstantiationError::class),
                requestCase(
                    "unbound-var-and-division-by-zero",
                    ArithmeticGreaterThan.functor("N", "/"(3, 0)),
                    InstantiationError::class,
                ),
            )
        }
    }

    /** Greater functor test data (input, [true | false | ErrorType]) */
    internal val greaterOrEqualQueryToResult by lazy {
        logicProgramming {
            listOf(
                requestCase(
                    "equal-expressions",
                    ArithmeticGreaterThanOrEqualTo.functor("*"(3, 2), "-"(7, 1)),
                    true,
                ),
                requestCase("real-vs-integer", ArithmeticGreaterThanOrEqualTo.functor(1.0, 1), true),
                requestCase("zero-vs-one", ArithmeticGreaterThanOrEqualTo.functor(0, 1), false),
                requestCase(
                    "approximated-third",
                    ArithmeticGreaterThanOrEqualTo.functor(0.333, "/"(1, 3)),
                    false,
                ),
                requestCase(
                    "unbound-var",
                    ArithmeticGreaterThanOrEqualTo.functor("X", 5),
                    InstantiationError::class,
                ),
                requestCase(
                    "unbound-var-and-division-by-zero",
                    ArithmeticGreaterThanOrEqualTo.functor("N", "/"(3, 0)),
                    InstantiationError::class,
                ),
            )
        }
    }

    /** Greater functor test data (input, [true | false | ErrorType]) */
    internal val lowerQueryToResult by lazy {
        logicProgramming {
            listOf(
                requestCase("zero-vs-one", ArithmeticLowerThan.functor(0, 1), true),
                requestCase("approximated-third", ArithmeticLowerThan.functor(0.333, "/"(1, 3)), true),
                requestCase("real-vs-integer", ArithmeticLowerThan.functor(1.0, 1), false),
                requestCase("equal-expressions", ArithmeticLowerThan.functor("*"(3, 2), "-"(7, 1)), false),
                requestCase("unbound-var", ArithmeticLowerThan.functor("X", 5), InstantiationError::class),
                requestCase(
                    "unbound-var-and-division-by-zero",
                    ArithmeticLowerThan.functor(1, "+"("N", "/"(3, 0))),
                    InstantiationError::class,
                ),
            )
        }
    }

    /** Greater functor test data (input, [true | false | ErrorType]) */
    internal val lowerOrEqualQueryToResult by lazy {
        logicProgramming {
            listOf(
                requestCase("zero-vs-one", ArithmeticLowerThanOrEqualTo.functor(0, 1), true),
                requestCase("real-vs-integer", ArithmeticLowerThanOrEqualTo.functor(1.0, 1), true),
                requestCase("approximated-third", ArithmeticLowerThanOrEqualTo.functor(0.333, "/"(1, 3)), true),
                requestCase(
                    "greater-expression",
                    ArithmeticLowerThanOrEqualTo.functor("*"(3, 2), "-"(6, 1)),
                    false,
                ),
                requestCase(
                    "unbound-var",
                    ArithmeticLowerThanOrEqualTo.functor("X", 5),
                    InstantiationError::class,
                ),
                requestCase(
                    "unbound-var-and-division-by-zero",
                    ArithmeticLowerThanOrEqualTo.functor("N", "/"(3, 0)),
                    InstantiationError::class,
                ),
            )
        }
    }
}
