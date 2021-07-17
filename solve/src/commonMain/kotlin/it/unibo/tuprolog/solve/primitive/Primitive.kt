package it.unibo.tuprolog.solve.primitive

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/** A typealias for a primitive function that accepts a [Solve.Request] and returns a Sequence of [Solve.Response]s */
fun interface Primitive {
    @JsName("solve")
    fun solve(request: Solve.Request<ExecutionContext>): Sequence<Solve.Response>

    companion object {
        @JsName("of")
        @JvmStatic
        fun of(function: (Solve.Request<ExecutionContext>) -> Sequence<Solve.Response>): Primitive = Primitive(function)

        /**
         * Creates a new [Primitive], behaving exactly as given [uncheckedPrimitive], but accepting only provided [supportedSignature]
         * as [Solve.Request] signature, throwing [IllegalArgumentException] otherwise
         */
        @JsName("enforcingSignature")
        @JvmStatic
        fun <C : ExecutionContext> enforcingSignature(
            supportedSignature: Signature,
            uncheckedPrimitive: (Solve.Request<C>) -> Sequence<Solve.Response>
        ): Primitive = Primitive {
            // TODO see TODO in "Signature"; here should be called that method to check if primitive could execute
            @Suppress("UNCHECKED_CAST")
            when (it.signature) {
                supportedSignature -> uncheckedPrimitive(it as Solve.Request<C>)
                else -> throw IllegalArgumentException("This primitive supports only this signature `$supportedSignature`")
            }
        }

        @JsName("enforcingSignatureForPrimitive")
        @JvmStatic
        fun enforcingSignature(
            supportedSignature: Signature,
            uncheckedPrimitive: Primitive
        ): Primitive = enforcingSignature<ExecutionContext>(supportedSignature, uncheckedPrimitive::solve)
    }
}
