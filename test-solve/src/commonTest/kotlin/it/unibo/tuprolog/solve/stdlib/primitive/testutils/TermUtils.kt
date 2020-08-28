package it.unibo.tuprolog.solve.stdlib.primitive.testutils

import it.unibo.tuprolog.dsl.prolog
import it.unibo.tuprolog.solve.stdlib.primitive.*
import it.unibo.tuprolog.solve.stdlib.primitive.testutils.PrimitiveUtils.createSolveRequest

/**
 * Utils singleton to help testing term comparison primitives
 */
internal object TermUtils {

    /** Equal functor test data (input, [true | false | ErrorType]) */
    internal val equalQueryToResult by lazy {
        prolog {
            mapOf(
                TermIdentical.functor(structOf("a", atomOf("c")),
                    structOf("a", atomOf("c"))) to true,
                TermIdentical.functor(structOf("a", atomOf("c")),
                    structOf("a", atomOf("x"))) to false,
                TermIdentical.functor(varOf("X"), varOf("X")) to true,
                TermIdentical.functor(varOf("X"), varOf("Y")) to false
            ).mapKeys { (query, _) -> createSolveRequest(query) }
        }
    }

    /** NotEqual functor test data (input, [true | false | ErrorType]) */
    internal val notEqualQueryToResult by lazy {
        prolog {
            mapOf(
                TermNotIdentical.functor(structOf("a", atomOf("c")),
                    structOf("a", atomOf("c"))) to false,
                TermNotIdentical.functor(structOf("a", atomOf("c")),
                    structOf("a", atomOf("x"))) to true,
                TermNotIdentical.functor(varOf("X"), varOf("X")) to false,
                TermNotIdentical.functor(varOf("X"), varOf("Y")) to true
            ).mapKeys { (query, _) -> createSolveRequest(query) }
        }
    }

    /** Greater functor test data (input, [true | false | ErrorType]) */
    internal val greaterQueryToResult by lazy {
        prolog {
            mapOf(
                TermGreaterThan.functor(structOf("a", atomOf("a")),
                    structOf("a", atomOf("a"))) to false,
                TermGreaterThan.functor(structOf("a", atomOf("a")),
                    structOf("a", atomOf("b"))) to false,
                TermGreaterThan.functor(structOf("a", atomOf("b")),
                    structOf("a", atomOf("a"))) to true,
                TermGreaterThan.functor(varOf("X"), varOf("X")) to false,
                TermGreaterThan.functor(atomOf("a"), varOf("Y")) to true,
                TermGreaterThan.functor(varOf("Y"), atomOf("a")) to false
            ).mapKeys { (query, _) -> createSolveRequest(query) }
        }
    }

    /** Greater functor test data (input, [true | false | ErrorType]) */
    internal val greaterOrEqualQueryToResult by lazy {
        prolog {
            mapOf(
                TermGreaterThanOrEqualTo.functor(structOf("a", atomOf("a")),
                    structOf("a", atomOf("a"))) to true,
                TermGreaterThanOrEqualTo.functor(structOf("a", atomOf("a")),
                    structOf("a", atomOf("b"))) to false,
                TermGreaterThanOrEqualTo.functor(structOf("a", atomOf("b")),
                    structOf("a", atomOf("a"))) to true,
                TermGreaterThanOrEqualTo.functor(varOf("X"), varOf("X")) to true,
                TermGreaterThanOrEqualTo.functor(atomOf("a"), varOf("Y")) to true,
                TermGreaterThanOrEqualTo.functor(varOf("Y"), atomOf("a")) to false
            ).mapKeys { (query, _) -> createSolveRequest(query) }
        }
    }

    /** Greater functor test data (input, [true | false | ErrorType]) */
    internal val lowerQueryToResult by lazy {
        prolog {
            mapOf(
                TermLowerThan.functor(structOf("a", atomOf("a")),
                    structOf("a", atomOf("a"))) to false,
                TermLowerThan.functor(structOf("a", atomOf("a")),
                    structOf("a", atomOf("b"))) to true,
                TermLowerThan.functor(structOf("a", atomOf("b")),
                    structOf("a", atomOf("a"))) to false,
                TermLowerThan.functor(varOf("X"), varOf("X")) to false,
                TermLowerThan.functor(atomOf("a"), varOf("Y")) to false,
                TermLowerThan.functor(varOf("Y"), atomOf("a")) to true
            ).mapKeys { (query, _) -> createSolveRequest(query) }
        }
    }

    /** Greater functor test data (input, [true | false | ErrorType]) */
    internal val lowerOrEqualQueryToResult by lazy {
        prolog {
            mapOf(
                TermLowerThanOrEqualTo.functor(structOf("a", atomOf("a")),
                    structOf("a", atomOf("a"))) to true,
                TermLowerThanOrEqualTo.functor(structOf("a", atomOf("a")),
                    structOf("a", atomOf("b"))) to true,
                TermLowerThanOrEqualTo.functor(structOf("a", atomOf("b")),
                    structOf("a", atomOf("a"))) to false,
                TermLowerThanOrEqualTo.functor(varOf("X"), varOf("X")) to true,
                TermLowerThanOrEqualTo.functor(atomOf("a"), varOf("Y")) to false,
                TermLowerThanOrEqualTo.functor(varOf("Y"), atomOf("a")) to true
            ).mapKeys { (query, _) -> createSolveRequest(query) }
        }
    }

}
