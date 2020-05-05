package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.solve.library.Libraries
import it.unibo.tuprolog.theory.ClauseDatabase
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
            solution: Solution,
            libraries: Libraries? = null,
            flags: PrologFlags? = null,
            staticKb: ClauseDatabase? = null,
            dynamicKb: ClauseDatabase? = null,
            sideEffectManager: SideEffectManager? = null,
            inputChannels: PrologInputChannels<*>? = null,
            outputChannels: PrologOutputChannels<*>? = null
        ) = when (solution) {
            is Solution.Yes -> replySuccess(
                solution.substitution,
                libraries, flags, staticKb, dynamicKb, sideEffectManager, inputChannels, outputChannels
            )
            is Solution.No -> replyFail(libraries, flags, staticKb, dynamicKb, sideEffectManager, inputChannels, outputChannels)
            is Solution.Halt -> replyException(
                solution.exception,
                libraries, flags, staticKb, dynamicKb, sideEffectManager, inputChannels, outputChannels
            )
        }

        /** Creates a new successful or failed [Response] depending on [condition]; to be used when the substitution doesn't change */
        @JsName("replyWithCondition")
        fun replyWith(
            condition: Boolean,
            libraries: Libraries? = null,
            flags: PrologFlags? = null,
            staticKb: ClauseDatabase? = null,
            dynamicKb: ClauseDatabase? = null,
            sideEffectManager: SideEffectManager? = null,
            inputChannels: PrologInputChannels<*>? = null,
            outputChannels: PrologOutputChannels<*>? = null
        ) = when (condition) {
            true -> replySuccess(
                libraries = libraries,
                flags = flags,
                staticKb = staticKb,
                dynamicKb = dynamicKb,
                sideEffectManager = sideEffectManager,
                inputChannels = inputChannels,
                outputChannels = outputChannels
            )
            false -> replyFail(libraries, flags, staticKb, dynamicKb, sideEffectManager, inputChannels, outputChannels)
        }

        /** Creates a new successful [Response] to this Request, with substitution */
        @JsName("replySuccess")
        fun replySuccess(
            substitution: Substitution.Unifier = Substitution.empty(),
            libraries: Libraries? = null,
            flags: PrologFlags? = null,
            staticKb: ClauseDatabase? = null,
            dynamicKb: ClauseDatabase? = null,
            sideEffectManager: SideEffectManager? = null,
            inputChannels: PrologInputChannels<*>? = null,
            outputChannels: PrologOutputChannels<*>? = null
        ) = Response(
                Solution.Yes(query, substitution),
                libraries,
                flags,
                staticKb,
                dynamicKb,
                sideEffectManager,
                inputChannels,
                outputChannels
            )

        /** Creates a new failed [Response] to this Request */
        @JsName("replyFail")
        fun replyFail(
            libraries: Libraries? = null,
            flags: PrologFlags? = null,
            staticKb: ClauseDatabase? = null,
            dynamicKb: ClauseDatabase? = null,
            sideEffectManager: SideEffectManager? = null,
            inputChannels: PrologInputChannels<*>? = null,
            outputChannels: PrologOutputChannels<*>? = null
        ) = Response(
                Solution.No(query),
                libraries,
                flags,
                staticKb,
                dynamicKb,
                sideEffectManager,
                inputChannels,
                outputChannels
            )

        /** Creates a new halt [Response] to this Request, with cause exception */
        @JsName("replyException")
        fun replyException(
            exception: TuPrologRuntimeException,
            libraries: Libraries? = null,
            flags: PrologFlags? = null,
            staticKb: ClauseDatabase? = null,
            dynamicKb: ClauseDatabase? = null,
            sideEffectManager: SideEffectManager? = null,
            inputChannels: PrologInputChannels<*>? = null,
            outputChannels: PrologOutputChannels<*>? = null
        ) = Response(
            Solution.Halt(query, exception),
            libraries,
            flags,
            staticKb,
            dynamicKb,
            sideEffectManager,
            inputChannels,
            outputChannels
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
        /** The set of loaded libraries after request execution (use `null` in case nothing changed) */
        @JsName("libraries")
        val libraries: Libraries? = null,
        /** The map of loaded flags after request execution (use `null` in case nothing changed) */
        @JsName("flags")
        val flags: PrologFlags? = null,
        /** The Static KB after request execution (use `null` in case nothing changed) */
        @JsName("staticKb")
        val staticKb: ClauseDatabase? = null,
        /** The Dynamic KB after request execution (use `null` in case nothing changed) */
        @JsName("dynamicKb")
        val dynamicKb: ClauseDatabase? = null,
        /** The Prolog flow modification manager after request execution (use `null` in case nothing changed) */
        @JsName("sideEffectManager")
        val sideEffectManager: SideEffectManager? = null,
        /** The input channels modification after request execution (use `null` in case nothing changed) */
        @JsName("inputChannels")
        val inputChannels: PrologInputChannels<*>? = null,
        /** The output channels flow modification after request execution (use `null` in case nothing changed) */
        @JsName("outputChannels")
        val outputChannels: PrologOutputChannels<*>? = null
    ) : Solve()
}
