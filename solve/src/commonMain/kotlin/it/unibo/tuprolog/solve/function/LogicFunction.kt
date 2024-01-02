package it.unibo.tuprolog.solve.function

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A typealias for a prolog function that accepts a [Compute.Request] and returns a [Compute.Response]
 *
 * @author Enrico
 */
fun interface LogicFunction {
    @JsName("compute")
    fun compute(request: Compute.Request<ExecutionContext>): Compute.Response

    companion object {
        @JsName("of")
        @JvmStatic
        fun of(function: (Compute.Request<ExecutionContext>) -> Compute.Response): LogicFunction = LogicFunction(function)

        /**
         * Creates a new [LogicFunction], behaving exactly as given [uncheckedFunction], but accepting only provided [supportedSignature]
         * as [Compute.Request] signature, throwing [IllegalArgumentException] otherwise
         */
        @JsName("enforcingSignature")
        @JvmStatic
        fun <C : ExecutionContext> enforcingSignature(
            supportedSignature: Signature,
            uncheckedFunction: (Compute.Request<C>) -> Compute.Response,
        ): LogicFunction =
            LogicFunction {
                // TODO see TODO in "Signature"; here should be called that method to check if primitive could execute
                @Suppress("UNCHECKED_CAST")
                when (it.signature) {
                    supportedSignature -> uncheckedFunction(it as Compute.Request<C>)
                    else -> throw IllegalArgumentException("This function supports only this signature `$supportedSignature`")
                }
            }

        @JsName("enforcingSignatureForLogicFunction")
        @JvmStatic
        fun enforcingSignature(
            supportedSignature: Signature,
            uncheckedFunction: LogicFunction,
        ): LogicFunction = enforcingSignature<ExecutionContext>(supportedSignature, uncheckedFunction::compute)
    }
}
