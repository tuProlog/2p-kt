package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.exception.ResolutionException
import it.unibo.tuprolog.utils.Castable
import it.unibo.tuprolog.utils.Taggable
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/** A type representing a solution to a goal */
sealed interface Solution :
    Taggable<Solution>,
    Castable<Solution> {
    /** The query to which the solution refers */
    @JsName("query")
    val query: Struct

    /** The substitution that has been applied to find the solution, or a failed substitution */
    @JsName("substitution")
    val substitution: Substitution

    @JsName("exception")
    val exception: ResolutionException?

    /** The [Struct] representing the solution, or `null` in case of a non-successful solution */
    @JsName("solvedQuery")
    val solvedQuery: Struct?

    @JsName("isYes")
    val isYes: Boolean

    @JsName("isNo")
    val isNo: Boolean

    @JsName("isHalt")
    val isHalt: Boolean

    /**
     * Casts the current [Solution] to [Yes], if possible, or returns `null` otherwise
     * @return the current [Solution], casted to [Yes], or `null`, if the current term is not an instance of [Yes]
     */
    @JsName("asYes")
    fun asYes(): Yes? = null

    /**
     * Casts the current [Solution] to [Yes], if possible
     * @throws ClassCastException if the current [Solution] is not an instance of [Yes]
     * @return the current [Solution], casted to [Yes]
     */
    @JsName("castToYes")
    fun castToYes(): Yes = asYes() ?: throw ClassCastException("Cannot cast $this to ${Yes::class.simpleName}")

    /**
     * Casts the current [Solution] to [No], if possible, or returns `null` otherwise
     * @return the current [Solution], casted to [No], or `null`, if the current term is not an instance of [No]
     */
    @JsName("asNo")
    fun asNo(): No? = null

    /**
     * Casts the current [Solution] to [No], if possible
     * @throws ClassCastException if the current [Solution] is not an instance of [No]
     * @return the current [Solution], casted to [No]
     */
    @JsName("castToNo")
    fun castToNo(): No = asNo() ?: throw ClassCastException("Cannot cast $this to ${No::class.simpleName}")

    /**
     * Casts the current [Solution] to [Halt], if possible, or returns `null` otherwise
     * @return the current [Solution], casted to [Halt], or `null`, if the current term is not an instance of [Halt]
     */
    @JsName("asHalt")
    fun asHalt(): Halt? = null

    /**
     * Casts the current [Solution] to [Halt], if possible
     * @throws ClassCastException if the current [Solution] is not an instance of [Halt]
     * @return the current [Solution], casted to [Halt]
     */
    @JsName("castToHalt")
    fun castToHalt(): Halt = asHalt() ?: throw ClassCastException("Cannot cast $this to ${Halt::class.simpleName}")

    @JsName("whenIs")
    fun <T> whenIs(
        yes: ((Yes) -> T)? = null,
        no: ((No) -> T)? = null,
        halt: ((Halt) -> T)? = null,
        otherwise: ((Solution) -> T) = { throw IllegalStateException("Cannot handle solution $it") },
    ): T {
        if (this is Solution.Yes && yes != null) {
            return yes(this)
        }
        if (this is Solution.No && no != null) {
            return no(this)
        }
        if (this is Solution.Halt && halt != null) {
            return halt(this)
        }
        return otherwise(this)
    }

    @JsName("cleanUp")
    fun cleanUp(): Solution

    @JsName("valueOf")
    fun valueOf(variable: Var): Term?

    @JsName("valueOfByName")
    fun valueOf(variable: String): Term?

    /** A type representing the successful solution */
    sealed interface Yes : Solution {
        override val substitution: Substitution.Unifier

        override val solvedQuery: Struct

        override fun replaceTags(tags: Map<String, Any>): Yes

        @JsName("copy")
        fun copy(
            query: Struct = this.query,
            substitution: Substitution.Unifier = this.substitution,
        ): Yes

        override fun cleanUp(): Yes

        override fun asYes(): Yes = this
    }

    /** A type representing a failed solution */
    sealed interface No : Solution {
        override val substitution: Substitution.Fail

        override val solvedQuery: Nothing?

        override fun replaceTags(tags: Map<String, Any>): No

        @JsName("copy")
        fun copy(query: Struct = this.query): No

        override fun cleanUp(): No

        override fun asNo(): No = this
    }

    /** A type representing a failed (halted) solution because of an exception */
    sealed interface Halt : Solution {
        override val exception: ResolutionException

        override fun replaceTags(tags: Map<String, Any>): Halt

        @JsName("copy")
        fun copy(
            query: Struct = this.query,
            exception: ResolutionException = this.exception,
        ): Halt

        override fun cleanUp(): Halt

        override fun asHalt(): Halt = this
    }

    companion object {
        @JvmStatic
        @JsName("yes")
        fun yes(
            query: Struct,
            substitution: Substitution.Unifier = Substitution.empty(),
        ): Yes = SolutionImpl.YesImpl(query, substitution)

        @JvmStatic
        @JsName("yesBySignature")
        fun yes(
            signature: Signature,
            arguments: List<Term>,
            substitution: Substitution.Unifier = Substitution.empty(),
        ): Yes = SolutionImpl.YesImpl(signature, arguments, substitution)

        @JvmStatic
        @JsName("no")
        fun no(query: Struct): No = SolutionImpl.NoImpl(query)

        @JvmStatic
        @JsName("noBySignature")
        fun no(
            signature: Signature,
            arguments: List<Term>,
        ): No = SolutionImpl.NoImpl(signature, arguments)

        @JvmStatic
        @JsName("halt")
        fun halt(
            query: Struct,
            exception: ResolutionException,
        ): Halt = SolutionImpl.HaltImpl(query, exception)

        @JvmStatic
        @JsName("haltBySignature")
        fun halt(
            signature: Signature,
            arguments: List<Term>,
            exception: ResolutionException,
        ): Halt = SolutionImpl.HaltImpl(signature, arguments, exception)
    }
}
