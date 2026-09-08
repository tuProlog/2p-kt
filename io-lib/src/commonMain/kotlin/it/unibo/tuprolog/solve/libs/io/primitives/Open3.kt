package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.open
import it.unibo.tuprolog.solve.primitive.Solve.Request
import it.unibo.tuprolog.solve.primitive.Solve.Response
import it.unibo.tuprolog.solve.primitive.TernaryRelation

/**
 * Implements ISO's `open/3`: opens the source/sink named by the first argument (parsed into a
 * [it.unibo.tuprolog.solve.libs.io.Url], both proper URLs and bare filesystem paths are accepted) in the mode named
 * by the second (`read`, `write`, or `append`, see [it.unibo.tuprolog.solve.libs.io.IOMode]), registers the
 * resulting channel under an auto-generated alias, and unifies the third argument with its `$stream(...)` term.
 * Equivalent to `open/4` with an empty options list; see [Open4] for the full contract, including exceptions.
 *
 * ```prolog
 * ?- open('theory.pl', read, Stream), read(Stream, Term), close(Stream).
 * ```
 */
object Open3 : TernaryRelation.NonBacktrackable<ExecutionContext>("open") {
    override fun Request<ExecutionContext>.computeOne(
        first: Term,
        second: Term,
        third: Term,
    ): Response = open(third)
}
