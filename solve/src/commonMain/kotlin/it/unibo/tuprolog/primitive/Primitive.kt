package it.unibo.tuprolog.primitive

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Solve

/** A typealias for a primitive function that accepts a [Solve.Request] and returns a Sequence of [Solve.Response]s */
typealias Primitive = (Solve.Request<ExecutionContext>) -> Sequence<Solve.Response>

/**
 * Creates a new [Primitive], behaving exactly as given [uncheckedPrimitive], but accepting only provided [supportedSignature]
 * as [Solve.Request] signature, throwing [IllegalArgumentException] otherwise
 */
fun primitiveOf(supportedSignature: Signature, uncheckedPrimitive: Primitive): Primitive = {
    when (it.signature) { // TODO see TODO in "Signature"; here should be called that method to check if primitive could execute
        supportedSignature -> uncheckedPrimitive(it)
        else -> throw IllegalArgumentException("This primitive supports only this signature `$supportedSignature`")
    }
}
