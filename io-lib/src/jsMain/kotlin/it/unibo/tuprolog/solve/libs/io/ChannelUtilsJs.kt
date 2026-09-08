package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.solve.channel.InputChannel

/**
 * JS implementation of [it.unibo.tuprolog.solve.libs.io.asTermChannel]: unimplemented. Term-level reading
 * (`read/1,2`, `read_term/2,3`) is not yet supported on this platform, so calling this always throws.
 * @throws NotImplementedError unconditionally.
 */
actual fun InputChannel<String>.asTermChannel(operators: OperatorSet): InputChannel<Term> {
    TODO("reading terms is still not supported for JS")
}
