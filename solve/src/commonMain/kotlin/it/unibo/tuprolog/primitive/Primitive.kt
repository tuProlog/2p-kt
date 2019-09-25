package it.unibo.tuprolog.primitive

import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.ExecutionContext

/** A typealias for a primitive function that accepts a [Solve.Request] and returns a Sequence of [Solve.Response]s */
typealias Primitive = (Solve.Request<ExecutionContext>) -> Sequence<Solve.Response> // TODO: 25/09/2019 reify Primitive to make ExecutionContext Type fall through classes

/**
 * Creates a new [Primitive], behaving exactly as given [uncheckedPrimitive], but accepting only provided [supportedSignature]
 * as [Solve.Request] signature, throwing [IllegalArgumentException] otherwise
 */
fun primitiveOf(supportedSignature: Signature, uncheckedPrimitive: Primitive): Primitive = {
    when (it.signature) {
        supportedSignature -> uncheckedPrimitive(it)
        else -> throw IllegalArgumentException("This primitive supports only this signature `$supportedSignature`")
    }
}
