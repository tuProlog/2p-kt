package it.unibo.tuprolog.function

import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.ExecutionContext

/**
 * A typealias for a prolog function that accepts a [Compute.Request] and returns a [Compute.Response]
 *
 * @author Enrico
 */
typealias PrologFunction = (Compute.Request<ExecutionContext>) -> Compute.Response

/**
 * Creates a new [PrologFunction], behaving exactly as given [uncheckedFunction], but accepting only provided [supportedSignature]
 * as [Compute.Request] signature, throwing [IllegalArgumentException] otherwise
 */
fun functionOf(supportedSignature: Signature, uncheckedFunction: PrologFunction): PrologFunction = {
    when (it.signature) { // TODO see TODO in "Signature"; here should be called that method to check if primitive could execute
        supportedSignature -> uncheckedFunction(it)
        else -> throw IllegalArgumentException("This function supports only this signature `$supportedSignature`")
    }
}
