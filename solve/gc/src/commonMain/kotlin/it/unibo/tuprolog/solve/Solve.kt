package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Atom
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
    data class Request(
            /** The user's query, i.e. the 0-level goal triggering resolution*/
            val query: Struct,
            /** The goal this [Request] needs to solve */
            val currentGoal: Struct,
            /** Signature of the goal to be solved */
            val signature: Signature,
            /** Arguments with which the goal is invoked */
            val arguments: List<Term>,
            /** The set of libraries loaded before primitive execution */
            val libraries: Libraries,
            /** The map of flags and their values loaded before primitive execution */
            val flags: Map<Atom, Term>,
            /** The Static KB loaded before primitive execution */
            val staticKB: ClauseDatabase,
            /** The Dynamic KB loaded before primitive execution */
            val dynamicKB: ClauseDatabase
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
        }

        fun toResponse(solution: Solution, libraries: Libraries? = null, flags: Map<Atom, Term>? = null, staticKB: ClauseDatabase? = null, dynamicKB: ClauseDatabase? = null) =
                Response(solution, libraries, flags, staticKB, dynamicKB)

        fun toSuccessfulResponse(substitution: Substitution.Unifier = Substitution.empty(), libraries: Libraries? = null, flags: Map<Atom, Term>? = null, staticKB: ClauseDatabase? = null, dynamicKB: ClauseDatabase? = null) =
                Response(Solution.Yes(query, substitution), libraries, flags, staticKB, dynamicKB)

        fun toFailingResponse(libraries: Libraries? = null, flags: Map<Atom, Term>? = null, staticKB: ClauseDatabase? = null, dynamicKB: ClauseDatabase? = null) =
                Response(Solution.No(query), libraries, flags, staticKB, dynamicKB)

        fun toExceptionalResponse(exception: TuPrologRuntimeException, libraries: Libraries? = null, flags: Map<Atom, Term>? = null, staticKB: ClauseDatabase? = null, dynamicKB: ClauseDatabase? = null) =
                Response(Solution.Halt(query, exception), libraries, flags, staticKB, dynamicKB)

        fun toResponse(condition: Boolean, libraries: Libraries? = null, flags: Map<Atom, Term>? = null, staticKB: ClauseDatabase? = null, dynamicKB: ClauseDatabase? = null) =
                if (condition) {
                    toSuccessfulResponse()
                } else {
                    toFailingResponse()
                }
    }


    /** Class representing a Response, from the Solver, to a [Solve.Request] */
    data class Response(
            /** The solution attached to the response */
            val solution: Solution,
            /** The set of loaded libraries after primitive execution (use `null` in case nothing changed) */
            val libraries: Libraries? = null,
            /** The map of loaded flags after primitive execution (use `null` in case nothing changed) */
            val flags: Map<Atom, Term>? = null,
            /** The Static KB after primitive execution (use `null` in case nothing changed) */
            val staticKB: ClauseDatabase? = null,
            /** The Dynamic KB after primitive execution (use `null` in case nothing changed) */
            val dynamicKB: ClauseDatabase? = null
        ) : Solve()
}
