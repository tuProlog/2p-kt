package it.unibo.tuprolog.solve.libs.io.primitives

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

/**
 * Registered for ISO conformance, but **not implemented**: this implementation only supports text streams (see
 * [it.unibo.tuprolog.solve.libs.io.primitives.IOPrimitiveUtils.propertiesOf], always reporting `type(text)`), so
 * byte-oriented `get_byte/1` always raises a [it.unibo.tuprolog.solve.exception.error.SystemError]. Use
 * [GetCode1]/[GetChar1] instead.
 * @throws it.unibo.tuprolog.solve.exception.error.SystemError unconditionally.
 */
object GetByte1 : UnaryPredicate.NonBacktrackable<ExecutionContext>("get_byte") {
    override fun Solve.Request<ExecutionContext>.computeOne(first: Term): Solve.Response = notSupported()
}
