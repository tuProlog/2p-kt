package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.solve.impl.SolutionImpl
import it.unibo.tuprolog.utils.Taggable
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/** A type representing a solution to a goal */
interface Solution : Taggable<Solution> {

    /** The query to which the solution refers */
    @JsName("query")
    val query: Struct

    /** The substitution that has been applied to find the solution, or a failed substitution */
    @JsName("substitution")
    val substitution: Substitution

    /** The [Struct] representing the solution, or `null` in case of a non-successful solution */
    @JsName("solvedQuery")
    val solvedQuery: Struct?

    @JsName("isYes")
    val isYes: Boolean

    @JsName("isNo")
    val isNo: Boolean

    @JsName("isHalt")
    val isHalt: Boolean

    @JsName("whenIs")
    fun <T> whenIs(
        yes: ((Yes) -> T)? = null,
        no: ((No) -> T)? = null,
        halt: ((Halt) -> T)? = null,
        otherwise: ((Solution) -> T) = { throw IllegalStateException("Cannot handle solution $it") }
    ): T

    /** A type representing the successful solution */
    interface Yes : Solution {
        override val substitution: Substitution.Unifier

        override val solvedQuery: Struct

        override fun replaceTags(tags: Map<String, Any>): Yes

        @JsName("copy")
        fun copy(query: Struct = this.query, substitution: Substitution.Unifier = this.substitution): Yes
    }

    /** A type representing a failed solution */
    interface No : Solution {
        override val substitution: Substitution.Fail

        override val solvedQuery: Nothing?

        override fun replaceTags(tags: Map<String, Any>): No

        @JsName("copy")
        fun copy(query: Struct = this.query): No
    }

    /** A type representing a failed (halted) solution because of an exception */
    interface Halt : Solution {
        @JsName("exception")
        val exception: TuPrologRuntimeException

        override fun replaceTags(tags: Map<String, Any>): Halt

        @JsName("copy")
        fun copy(query: Struct = this.query, exception: TuPrologRuntimeException = this.exception): Halt
    }

    companion object {
        @JvmStatic
        @JsName("yes")
        fun yes(query: Struct, substitution: Substitution.Unifier = Substitution.empty()): Yes =
            SolutionImpl.YesImpl(query, substitution)

        @JvmStatic
        @JsName("yesBySignature")
        fun yes(
            signature: Signature,
            arguments: List<Term>,
            substitution: Substitution.Unifier = Substitution.empty()
        ): Yes = SolutionImpl.YesImpl(signature, arguments, substitution)

        @JvmStatic
        @JsName("no")
        fun no(query: Struct): No = SolutionImpl.NoImpl(query)

        @JvmStatic
        @JsName("noBySignature")
        fun no(signature: Signature, arguments: List<Term>): No = SolutionImpl.NoImpl(signature, arguments)

        @JvmStatic
        @JsName("halt")
        fun halt(query: Struct, exception: TuPrologRuntimeException): Halt = SolutionImpl.HaltImpl(query, exception)

        @JvmStatic
        @JsName("haltBySignature")
        fun halt(
            signature: Signature,
            arguments: List<Term>,
            exception: TuPrologRuntimeException,
        ): Halt = SolutionImpl.HaltImpl(signature, arguments, exception)
    }
}
