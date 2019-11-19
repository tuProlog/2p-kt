package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.libraries.Libraries
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.theory.ClauseDatabase

/** A base class for Solve requests and responses */
sealed class Solve {

    /** Class representing a Request to be full-filled by the Solver */
    data class Request<out C : ExecutionContext>(
        /** Signature of the goal to be solved in this [Request] */
        val signature: Signature,
        /** Arguments with which the goal is invoked in this [Request] */
        val arguments: List<Term>,
        /** The context that's current at Request making */
        val context: C,
        /** The time instant when the request was submitted for resolution */
        val requestIssuingInstant: TimeInstant = currentTimeInstant(),
        /** The execution max duration after which the computation should end, because no more useful */
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
        val query: Struct by lazy { signature withArgs arguments }

        /** Creates a new [Response] to this Request */
        fun replyWith(
            solution: Solution,
            libraries: Libraries? = null,
            flags: PrologFlags? = null,
            staticKB: ClauseDatabase? = null,
            dynamicKB: ClauseDatabase? = null,
            sideEffectManager: SideEffectManager? = null
        ) = when (solution) {
            is Solution.Yes -> replySuccess(
                solution.substitution,
                libraries, flags, staticKB, dynamicKB, sideEffectManager
            )
            is Solution.No -> replyFail(libraries, flags, staticKB, dynamicKB, sideEffectManager)
            is Solution.Halt -> replyException(
                solution.exception,
                libraries, flags, staticKB, dynamicKB, sideEffectManager
            )
        }

        /** Creates a new successful or failed [Response] depending on [condition]; to be used when the substitution doesn't change */
        fun replyWith(
            condition: Boolean,
            libraries: Libraries? = null,
            flags: PrologFlags? = null,
            staticKB: ClauseDatabase? = null,
            dynamicKB: ClauseDatabase? = null,
            sideEffectManager: SideEffectManager? = null
        ) = when (condition) {
            true -> replySuccess(
                libraries = libraries,
                flags = flags,
                staticKB = staticKB,
                dynamicKB = dynamicKB,
                sideEffectManager = sideEffectManager
            )
            false -> replyFail(libraries, flags, staticKB, dynamicKB, sideEffectManager)
        }

        /** Creates a new successful [Response] to this Request, with substitution */
        fun replySuccess(
            substitution: Substitution.Unifier = Substitution.empty(),
            libraries: Libraries? = null,
            flags: PrologFlags? = null,
            staticKB: ClauseDatabase? = null,
            dynamicKB: ClauseDatabase? = null,
            sideEffectManager: SideEffectManager? = null
        ) = Response(Solution.Yes(query, substitution), libraries, flags, staticKB, dynamicKB, sideEffectManager)

        /** Creates a new failed [Response] to this Request */
        fun replyFail(
            libraries: Libraries? = null,
            flags: PrologFlags? = null,
            staticKB: ClauseDatabase? = null,
            dynamicKB: ClauseDatabase? = null,
            sideEffectManager: SideEffectManager? = null
        ) = Response(Solution.No(query), libraries, flags, staticKB, dynamicKB, sideEffectManager)

        /** Creates a new halt [Response] to this Request, with cause exception */
        fun replyException(
            exception: TuPrologRuntimeException,
            libraries: Libraries? = null,
            flags: PrologFlags? = null,
            staticKB: ClauseDatabase? = null,
            dynamicKB: ClauseDatabase? = null,
            sideEffectManager: SideEffectManager? = null
        ) = Response(Solution.Halt(query, exception), libraries, flags, staticKB, dynamicKB, sideEffectManager)
    }


    /** Class representing a Response, from the Solver, to a [Solve.Request] */
    data class Response(
        /** The solution attached to the response */
        val solution: Solution,
        /** The set of loaded libraries after request execution (use `null` in case nothing changed) */
        val libraries: Libraries? = null,
        /** The map of loaded flags after request execution (use `null` in case nothing changed) */
        val flags: PrologFlags? = null,
        /** The Static KB after request execution (use `null` in case nothing changed) */
        val staticKB: ClauseDatabase? = null,
        /** The Dynamic KB after request execution (use `null` in case nothing changed) */
        val dynamicKB: ClauseDatabase? = null,
        /** The Prolog flow modification manager after request execution (use `null` in case nothing changed) */
        val sideEffectManager: SideEffectManager? = null
    ) : Solve()
}
