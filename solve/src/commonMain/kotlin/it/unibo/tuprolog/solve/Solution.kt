package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException

/** A class representing a solution to a goal */
sealed class Solution {

    /** The query to which the solution refers */
    abstract val query: Struct
    /** The substitution that has been applied to find the solution, or a `failed` substitution */
    open val substitution: Substitution = Substitution.failed()
    /** The Struct representing the solution, or `null` if no solution is available */
    open val solvedQuery: Struct? = null

    /** A class representing the successful solution */
    data class Yes(
            override val query: Struct,
            /** The successful substitution applied finding the solution */
            override val substitution: Substitution.Unifier = Substitution.empty()
    ) : Solution() {

        constructor(signature: Signature, arguments: List<Term>, substitution: Substitution.Unifier = Substitution.empty())
                : this(signature withArgs arguments, substitution) {

            // a solution always refers to a fully instantiated Struct, that cannot have a vararg Signature
            noVarargSignatureCheck(signature)
        }

        /** The Struct representing the solution */
        override val solvedQuery: Struct by lazy { substitution.applyTo(query) as Struct }
    }

    /** A class representing a failed solution */
    data class No(override val query: Struct) : Solution() {

        constructor(signature: Signature, arguments: List<Term>) : this(signature withArgs arguments) {
            noVarargSignatureCheck(signature)
        }
    }

    /** A class representing a failed (halted) solution because of an exception */
    data class Halt(
            override val query: Struct,
            /** The exception that made the resolution to halt */
            val exception: TuPrologRuntimeException
    ) : Solution() {

        constructor(signature: Signature, arguments: List<Term>, exception: TuPrologRuntimeException)
                : this(signature withArgs arguments, exception) {
            noVarargSignatureCheck(signature)
        }
    }

    /** Internal function to check signature validity in constructing Solution instances */
    protected fun noVarargSignatureCheck(signature: Signature) =
            require(!signature.vararg) {
                "The signature should be a well-formed indicator, not vararg `$signature`"
            }
}