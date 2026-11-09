package it.unibo.tuprolog.solve.function

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
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
        /** The context that's current at Request making; its chronometer times the resolution, if needed */
        @JsName("context")
        val context: C,
    ) : Compute() {
        init {
            when {
                signature.vararg ->
                    require(arguments.count() >= signature.arity) {
                        "Trying to create Compute.Request of signature `$signature` " +
                            "with not enough arguments ${arguments.toList()}"
                    }
                else ->
                    require(arguments.count() == signature.arity) {
                        "Trying to create Compute.Request of signature `$signature` " +
                            "with wrong number of arguments ${arguments.toList()}"
                    }
            }
        }

        /** The current expression [Struct] of this request */
        @JsName("query")
        val query: Struct by lazy { signature withArgs arguments }

        /** Creates a new [Response] to this Request */
        @JsName("replyWith")
        fun replyWith(result: Term) = Response(result, context)
    }

    /** Class representing a Response, from the Expression evaluator, to a [Request] */
    data class Response(
        /** The result of evaluation process */
        @JsName("result")
        val result: Term,
        /**
         * The context the response was produced in, i.e. its request's: a read-only view, valid as long as the
         * response is (see [ExecutionContext]).
         */
        @JsName("context")
        val context: ExecutionContext,
    ) : Compute()
}
