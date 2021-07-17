package it.unibo.tuprolog.solve.function

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import kotlin.js.JsName

/**
 * A typealias for a prolog function that accepts a [Compute.Request] and returns a [Compute.Response]
 *
 * @author Enrico
 */
typealias LogicFunction = (Compute.Request<ExecutionContext>) -> Compute.Response

/**
 * Creates a new [LogicFunction], behaving exactly as given [uncheckedFunction], but accepting only provided [supportedSignature]
 * as [Compute.Request] signature, throwing [IllegalArgumentException] otherwise
 */
@JsName("functionOf")
fun functionOf(supportedSignature: Signature, uncheckedFunction: LogicFunction): LogicFunction = {
    when (it.signature) { // TODO see TODO in "Signature"; here should be called that method to check if primitive could execute
        supportedSignature -> uncheckedFunction(it)
        else -> throw IllegalArgumentException("This function supports only this signature `$supportedSignature`")
    }
}
