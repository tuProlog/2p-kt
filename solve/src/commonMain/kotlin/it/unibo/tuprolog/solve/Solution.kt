package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.solve.exception.TuprologRuntimeException

/** A class representing a solution to a goal */
sealed class Solution {

    /** The query to which the solution refers */
    abstract val query: Struct
    /** The substitution that has been applied to find the solution, or a `failed` substitution */
    abstract val substitution: Substitution
    /** The Struct representing the solution, or `null` if no solution is available */
    abstract val solvedQuery: Struct?

    /** A class representing the successful solution */
    data class Yes(
            override val query: Struct,
            /** The successful substitution applied finding the solution */
            override val substitution: Substitution.Unifier
    ) : Solution() {

        constructor(signature: Signature, arguments: List<Term>, substitution: Substitution.Unifier)
                : this(signature.withArgs(arguments) ?: Truth.fail(), substitution) {
            noVarargSignatureCheck(signature)
        }

        /** The Struct representing the solution */
        override val solvedQuery: Struct by lazy {
            substitution.applyTo(query) as Struct
        }
    }

    /** A class representing a failed solution */
    data class No(override val query: Struct) : Solution() {

        constructor(signature: Signature, arguments: List<Term>)
                : this(signature.withArgs(arguments) ?: Truth.fail()) {
            noVarargSignatureCheck(signature)
        }

        /** Always [Substitution.Fail] because no solution was found */
        override val substitution: Substitution.Fail = Substitution.failed()
        /** Always `null` because no solution was found */
        override val solvedQuery: Struct? = null
    }

    /** A class representing a failed (halted) solution because of an exception */
    data class Halt(override val query: Struct, val exception: TuprologRuntimeException) : Solution() {

        constructor(signature: Signature, arguments: List<Term>, exception: TuprologRuntimeException)
                : this(signature.withArgs(arguments) ?: Truth.fail(), exception) {
            noVarargSignatureCheck(signature)
        }

        /** Always [Substitution.Fail] because no solution was found */
        override val substitution: Substitution.Fail = Substitution.failed()
        /** Always `null` because no solution was found */
        override val solvedQuery: Struct? = null
    }

    /** Internal function to check signature validity in constructing Solution instances */
    protected fun noVarargSignatureCheck(signature: Signature) =
            require(!signature.vararg) {
                "The signature should be a well-formed indicator, not vararg `$signature`"
            }
}