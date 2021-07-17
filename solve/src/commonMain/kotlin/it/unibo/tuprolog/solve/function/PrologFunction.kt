package it.unibo.tuprolog.solve.function

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.function.LogicFunction
import kotlin.js.JsName

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
        fun of(function: (Compute.Request<ExecutionContext>) -> Compute.Response): LogicFunction =
            LogicFunction(function)

        /**
         * Creates a new [LogicFunction], behaving exactly as given [uncheckedFunction], but accepting only provided [supportedSignature]
         * as [Compute.Request] signature, throwing [IllegalArgumentException] otherwise
         */
        @JsName("enforcingSignature")
        fun <C : ExecutionContext> enforcingSignature(
            supportedSignature: Signature,
            uncheckedFunction: (Compute.Request<C>) -> Compute.Response
        ): LogicFunction = LogicFunction {
            // TODO see TODO in "Signature"; here should be called that method to check if primitive could execute
            @Suppress("UNCHECKED_CAST")
            when (it.signature) {
                supportedSignature -> uncheckedFunction(it as Compute.Request<C>)
                else -> throw IllegalArgumentException("This function supports only this signature `$supportedSignature`")
            }
        }
    }
}
