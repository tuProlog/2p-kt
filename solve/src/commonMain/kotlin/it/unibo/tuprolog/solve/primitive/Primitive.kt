package it.unibo.tuprolog.solve.primitive

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Helpable
import it.unibo.tuprolog.solve.Signature
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/** A typealias for a primitive function that accepts a [Solve.Request] and returns a Sequence of [Solve.Response]s */
fun interface Primitive : Helpable {
    @JsName("solve")
    fun solve(request: Solve.Request<ExecutionContext>): Sequence<Solve.Response>

    /** How solvers may pull this primitive's answers; [Alternatives.ON_DEMAND] unless declared otherwise. */
    @JsName("alternatives")
    val alternatives: Alternatives
        get() = Alternatives.ON_DEMAND

    companion object {
        @JsName("of")
        @JvmStatic
        fun of(function: (Solve.Request<ExecutionContext>) -> Sequence<Solve.Response>): Primitive = Primitive(function)

        /** A [Primitive] running [function], whose answers solvers pull as [alternatives] says. */
        @JsName("ofWithAlternatives")
        @JvmStatic
        fun of(
            alternatives: Alternatives,
            function: (Solve.Request<ExecutionContext>) -> Sequence<Solve.Response>,
        ): Primitive =
            object : Primitive {
                override val alternatives: Alternatives = alternatives

                override fun solve(request: Solve.Request<ExecutionContext>) = function(request)
            }

        /**
         * Creates a new [Primitive], behaving exactly as given [uncheckedPrimitive], but accepting only provided [supportedSignature]
         * as [Solve.Request] signature, throwing [IllegalArgumentException] otherwise
         */
        @JsName("enforcingSignature")
        @JvmStatic
        fun <C : ExecutionContext> enforcingSignature(
            supportedSignature: Signature,
            uncheckedPrimitive: (Solve.Request<C>) -> Sequence<Solve.Response>,
        ): Primitive =
            Primitive {
                // TODO see TODO in "Signature"; here should be called that method to check if primitive could execute
                @Suppress("UNCHECKED_CAST")
                when (it.signature) {
                    supportedSignature -> uncheckedPrimitive(it as Solve.Request<C>)
                    else -> throw IllegalArgumentException(
                        "This primitive supports only this signature `$supportedSignature`",
                    )
                }
            }

        @JsName("enforcingSignatureForPrimitive")
        @JvmStatic
        fun enforcingSignature(
            supportedSignature: Signature,
            uncheckedPrimitive: Primitive,
        ): Primitive =
            of(
                uncheckedPrimitive.alternatives,
                enforcingSignature<ExecutionContext>(supportedSignature, uncheckedPrimitive::solve)::solve,
            )
    }
}
