package it.unibo.tuprolog.primitive

import it.unibo.tuprolog.solve.Solve

/** A typealias for a primitive function that accepts a [Solve.Request] and returns a Sequence of [Solve.Response]s */
typealias Primitive = (Solve.Request) -> Sequence<Solve.Response>

/** Creates a [Primitive] that accepts only provided [supportedSignature] as [Solve.Request] signature, or throws [IllegalArgumentException] otherwise,
 *  behaving exactly as [uncheckedPrimitive] */
fun checkedPrimitiveFor(supportedSignature: Signature, uncheckedPrimitive: Primitive): Primitive = {
    when (it.signature) {
        supportedSignature -> uncheckedPrimitive(it)
        else -> throw IllegalArgumentException("This primitive supports only this signature `$supportedSignature`")
    }
}
