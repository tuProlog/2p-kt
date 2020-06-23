package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import kotlin.js.JsName

/** A base class for Solve requests and responses */
sealed class Solve {

    /** Class representing a Request to be full-filled by the Solver */
    data class Request<out C : ExecutionContext>(
        /** Signature of the goal to be solved in this [Request] */
        @JsName("signature")
        val signature: Signature,
        /** Arguments with which the goal is invoked in this [Request] */
        @JsName("arguments")
        val arguments: List<Term>,
        /** The context that's current at Request making */
        @JsName("context")
        val context: C,
        /** The time instant when the request was submitted for resolution */
        @JsName("requestIssuingInstant")
        val requestIssuingInstant: TimeInstant = currentTimeInstant(),
        /** The execution max duration after which the computation should end, because no more useful */
        @JsName("executionMaxDuration")
        val executionMaxDuration: TimeDuration = TimeDuration.MAX_VALUE
    ) : Solve() {
        init {
            when {
                signature.vararg -> require(arguments.count() >= signature.arity) {
                    "Trying to create Solve.Request of signature `$signature` with not enough arguments ${arguments.toList()}"
                }
                else -> require(arguments.count() == signature.arity) {
                    "Trying to create Solve.Request of signature `$signature` with wrong number of arguments ${arguments.toList()}"
                }
            }
            require(requestIssuingInstant >= 0) { "The request issuing instant can't be negative: $requestIssuingInstant" }
            require(executionMaxDuration >= 0) { "The execution max duration can't be negative: $executionMaxDuration" }
        }

        /** The current query [Struct] of this request */
        @JsName("query")
        val query: Struct by lazy { signature withArgs arguments }

        /** Creates a new [Response] to this Request */
        @JsName("replyWith")
        fun replyWith(
            substitution: Substitution,
            sideEffectManager: SideEffectManager? = null,
            vararg sideEffects: SideEffect
        ) = when (substitution) {
            is Substitution.Unifier -> replySuccess(substitution, sideEffectManager, *sideEffects)
            else -> replyFail(sideEffectManager, *sideEffects)
        }

        /** Creates a new [Response] to this Request */
        @JsName("replyWithSolution")
        fun replyWith(
            solution: Solution,
            sideEffectManager: SideEffectManager? = null,
            vararg sideEffects: SideEffect
        ) = Response(solution, sideEffectManager, *sideEffects)

        /** Creates a new successful or failed [Response] depending on [condition]; to be used when the substitution doesn't change */
        @JsName("replyWithCondition")
        fun replyWith(
            condition: Boolean,
            sideEffectManager: SideEffectManager? = null,
            vararg sideEffects: SideEffect
        ) = if (condition) {
            replySuccess(Substitution.empty(), sideEffectManager, *sideEffects)
        } else {
            replyFail(sideEffectManager, *sideEffects)
        }

        /** Creates a new successful [Response] to this Request, with substitution */
        @JsName("replySuccess")
        fun replySuccess(
            substitution: Substitution.Unifier = Substitution.empty(),
            sideEffectManager: SideEffectManager? = null,
            vararg sideEffects: SideEffect
        ) = Response(
                Solution.Yes(query, substitution),
                sideEffectManager,
                *sideEffects
            )

        /** Creates a new failed [Response] to this Request */
        @JsName("replyFail")
        fun replyFail(
            sideEffectManager: SideEffectManager? = null,
            vararg sideEffects: SideEffect
        ) = Response(
                Solution.No(query),
                sideEffectManager,
            *sideEffects
            )

        /** Creates a new halt [Response] to this Request, with cause exception */
        @JsName("replyException")
        fun replyException(
            exception: TuPrologRuntimeException,
            sideEffectManager: SideEffectManager? = null,
            vararg sideEffects: SideEffect
        ) = Response(
            Solution.Halt(query, exception),
            sideEffectManager,
            *sideEffects
        )

        @JsName("subSolver")
        fun subSolver(): Solver {
            return context.createSolver()
        }

        @JsName("solve")
        fun solve(goal: Struct, maxDuration: TimeDuration = TimeDuration.MAX_VALUE): Sequence<Solution> {
            return subSolver().solve(goal, maxDuration)
        }
    }


    /** Class representing a Response, from the Solver, to a [Solve.Request] */
    data class Response(
        /** The solution attached to the response */
        @JsName("solution")
        val solution: Solution,
        /** The Prolog flow modification manager after request execution (use `null` in case nothing changed) */
        @JsName("sideEffectManager")
        val sideEffectManager: SideEffectManager? = null,
        /** The (possibly empty) [List] of [SideEffect]s to be applied to the execution context after a primitive has been
         * executed */
        @JsName("sideEffects")
        val sideEffects: List<SideEffect> = emptyList()
    ) : Solve() {
        constructor(
            solution: Solution,
            sideEffectManager: SideEffectManager? = null,
            sideEffects: Iterable<SideEffect> = emptyList()
        ) : this(solution, sideEffectManager, sideEffects as? List<SideEffect> ?: sideEffects.toList())

        constructor(
            solution: Solution,
            sideEffectManager: SideEffectManager? = null,
            sideEffects: Sequence<SideEffect> = emptySequence()
        ) : this(solution, sideEffectManager, sideEffects.asIterable())

        constructor(
            solution: Solution,
            sideEffectManager: SideEffectManager? = null,
            vararg sideEffects: SideEffect
        ) : this(solution, sideEffectManager, listOf(*sideEffects))
    }
}
