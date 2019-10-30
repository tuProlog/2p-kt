package it.unibo.tuprolog.primitive.function

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.*

/** A base class for Computation requests and responses */
sealed class Compute {

    /** Class representing a Request to be full-filled by the Expression evaluator */
    data class Request<out C : ExecutionContext>(
            /** Signature of the function to be executed in this [Request] */
            val signature: Signature,
            /** Arguments with which the function is invoked in this [Request] */
            val arguments: List<Term>,
            /** The context that's current at Request making */
            val context: C,
            /** The time instant when the request was submitted for evaluation */
            val requestIssuingInstant: TimeInstant = currentTimeInstant(),
            /** The execution max duration after which the computation should end, because no more useful */
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
        val query: Struct by lazy { signature withArgs arguments }

        /** Creates a new [Response] to this Request */
        fun replyWith(result: Term) = Response(result)
    }


    /** Class representing a Response, from the Expression evaluator, to a [Solve.Request] */
    data class Response(
            /** The result of evaluation process */
            val result: Term
    ) : Compute()
}
