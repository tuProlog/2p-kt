package it.unibo.tuprolog.solve.stdlib.primitive.testutils

import it.unibo.tuprolog.dsl.logicProgramming
import it.unibo.tuprolog.solve.stdlib.primitive.TermGreaterThan
import it.unibo.tuprolog.solve.stdlib.primitive.TermGreaterThanOrEqualTo
import it.unibo.tuprolog.solve.stdlib.primitive.TermIdentical
import it.unibo.tuprolog.solve.stdlib.primitive.TermLowerThan
import it.unibo.tuprolog.solve.stdlib.primitive.TermLowerThanOrEqualTo
import it.unibo.tuprolog.solve.stdlib.primitive.TermNotIdentical
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.PrimitiveUtils.requestCase

/**
 * Utils singleton to help testing term comparison primitives
 */
internal object TermUtils {
    /** Equal functor test data (input, [true | false | ErrorType]) */
    internal val equalQueryToResult by lazy {
        logicProgramming {
            listOf(
                requestCase(
                    "same-struct",
                    TermIdentical.functor(structOf("a", atomOf("c")), structOf("a", atomOf("c"))),
                    true,
                ),
                requestCase(
                    "different-struct-args",
                    TermIdentical.functor(structOf("a", atomOf("c")), structOf("a", atomOf("x"))),
                    false,
                ),
                requestCase("same-var", TermIdentical.functor(varOf("X"), varOf("X")), true),
                requestCase("different-vars", TermIdentical.functor(varOf("X"), varOf("Y")), false),
            )
        }
    }

    /** NotEqual functor test data (input, [true | false | ErrorType]) */
    internal val notEqualQueryToResult by lazy {
        logicProgramming {
            listOf(
                requestCase(
                    "same-struct",
                    TermNotIdentical.functor(structOf("a", atomOf("c")), structOf("a", atomOf("c"))),
                    false,
                ),
                requestCase(
                    "different-struct-args",
                    TermNotIdentical.functor(structOf("a", atomOf("c")), structOf("a", atomOf("x"))),
                    true,
                ),
                requestCase("same-var", TermNotIdentical.functor(varOf("X"), varOf("X")), false),
                requestCase("different-vars", TermNotIdentical.functor(varOf("X"), varOf("Y")), true),
            )
        }
    }

    /** Greater functor test data (input, [true | false | ErrorType]) */
    internal val greaterQueryToResult by lazy {
        logicProgramming {
            listOf(
                requestCase(
                    "same-struct",
                    TermGreaterThan.functor(structOf("a", atomOf("a")), structOf("a", atomOf("a"))),
                    false,
                ),
                requestCase(
                    "lower-struct-arg",
                    TermGreaterThan.functor(structOf("a", atomOf("a")), structOf("a", atomOf("b"))),
                    false,
                ),
                requestCase(
                    "greater-struct-arg",
                    TermGreaterThan.functor(structOf("a", atomOf("b")), structOf("a", atomOf("a"))),
                    true,
                ),
                requestCase("same-var", TermGreaterThan.functor(varOf("X"), varOf("X")), false),
                requestCase("atom-vs-var", TermGreaterThan.functor(atomOf("a"), varOf("Y")), true),
                requestCase("var-vs-atom", TermGreaterThan.functor(varOf("Y"), atomOf("a")), false),
            )
        }
    }

    /** Greater functor test data (input, [true | false | ErrorType]) */
    internal val greaterOrEqualQueryToResult by lazy {
        logicProgramming {
            listOf(
                requestCase(
                    "same-struct",
                    TermGreaterThanOrEqualTo.functor(structOf("a", atomOf("a")), structOf("a", atomOf("a"))),
                    true,
                ),
                requestCase(
                    "lower-struct-arg",
                    TermGreaterThanOrEqualTo.functor(structOf("a", atomOf("a")), structOf("a", atomOf("b"))),
                    false,
                ),
                requestCase(
                    "greater-struct-arg",
                    TermGreaterThanOrEqualTo.functor(structOf("a", atomOf("b")), structOf("a", atomOf("a"))),
                    true,
                ),
                requestCase("same-var", TermGreaterThanOrEqualTo.functor(varOf("X"), varOf("X")), true),
                requestCase("atom-vs-var", TermGreaterThanOrEqualTo.functor(atomOf("a"), varOf("Y")), true),
                requestCase("var-vs-atom", TermGreaterThanOrEqualTo.functor(varOf("Y"), atomOf("a")), false),
            )
        }
    }

    /** Greater functor test data (input, [true | false | ErrorType]) */
    internal val lowerQueryToResult by lazy {
        logicProgramming {
            listOf(
                requestCase(
                    "same-struct",
                    TermLowerThan.functor(structOf("a", atomOf("a")), structOf("a", atomOf("a"))),
                    false,
                ),
                requestCase(
                    "lower-struct-arg",
                    TermLowerThan.functor(structOf("a", atomOf("a")), structOf("a", atomOf("b"))),
                    true,
                ),
                requestCase(
                    "greater-struct-arg",
                    TermLowerThan.functor(structOf("a", atomOf("b")), structOf("a", atomOf("a"))),
                    false,
                ),
                requestCase("same-var", TermLowerThan.functor(varOf("X"), varOf("X")), false),
                requestCase("atom-vs-var", TermLowerThan.functor(atomOf("a"), varOf("Y")), false),
                requestCase("var-vs-atom", TermLowerThan.functor(varOf("Y"), atomOf("a")), true),
            )
        }
    }

    /** Greater functor test data (input, [true | false | ErrorType]) */
    internal val lowerOrEqualQueryToResult by lazy {
        logicProgramming {
            listOf(
                requestCase(
                    "same-struct",
                    TermLowerThanOrEqualTo.functor(structOf("a", atomOf("a")), structOf("a", atomOf("a"))),
                    true,
                ),
                requestCase(
                    "lower-struct-arg",
                    TermLowerThanOrEqualTo.functor(structOf("a", atomOf("a")), structOf("a", atomOf("b"))),
                    true,
                ),
                requestCase(
                    "greater-struct-arg",
                    TermLowerThanOrEqualTo.functor(structOf("a", atomOf("b")), structOf("a", atomOf("a"))),
                    false,
                ),
                requestCase("same-var", TermLowerThanOrEqualTo.functor(varOf("X"), varOf("X")), true),
                requestCase("atom-vs-var", TermLowerThanOrEqualTo.functor(atomOf("a"), varOf("Y")), false),
                requestCase("var-vs-atom", TermLowerThanOrEqualTo.functor(varOf("Y"), atomOf("a")), true),
            )
        }
    }
}
