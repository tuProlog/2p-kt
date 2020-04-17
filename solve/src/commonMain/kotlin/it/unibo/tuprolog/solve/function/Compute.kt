package it.unibo.tuprolog.solve.function

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.*
import kotlin.js.JsName

/** A base class for Computation requests and responses */
sealed class Compute {

    /** Class representing a Request to be full-filled by the Expression evaluator */
    data class Request<out C : ExecutionContext>(
        /** Signature of the function to be executed in this [Request] */
        @JsName("signature")
        val signature: Signature,
        /** Arguments with which the function is invoked in this [Request] */
        @JsName("arguments")
        val arguments: List<Term>,
        /** The context that's current at Request making */
        @JsName("context")
        val context: C,
        /** The time instant when the request was submitted for evaluation */
        @JsName("requestIssuingInstant")
        val requestIssuingInstant: TimeInstant = currentTimeInstant(),
        /** The execution max duration after which the computation should end, because no more useful */
        @JsName("executionMaxDuration")
        val executionMaxDuration: TimeDuration = TimeDuration.MAX_VALUE
    ) : Compute() {
        init {
            when {
                signature.vararg -> require(arguments.count() >= signature.arity) {
                    "Trying to create Compute.Request of signature `$signature` with not enough arguments ${arguments.toList()}"
                }
                else -> require(arguments.count() == signature.arity) {
                    "Trying to create Compute.Request of signature `$signature` with wrong number of arguments ${arguments.toList()}"
                }
            }
            require(requestIssuingInstant >= 0) { "The request issuing instant can't be negative: $requestIssuingInstant" }
            require(executionMaxDuration >= 0) { "The execution max duration can't be negative: $executionMaxDuration" }
        }

        /** The current expression [Struct] of this request */
        @JsName("query")
        val query: Struct by lazy { signature withArgs arguments }

        /** Creates a new [Response] to this Request */
        @JsName("replyWith")
        fun replyWith(result: Term) = Response(result)
    }


    /** Class representing a Response, from the Expression evaluator, to a [Solve.Request] */
    data class Response(
        /** The result of evaluation process */
        @JsName("result")
        val result: Term
    ) : Compute()
}
