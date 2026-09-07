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

/**
 * A type representing a solution to a goal, as produced by [it.unibo.tuprolog.solve.Solver.solve].
 *
 * Every [Solution] is one of three sealed subtypes, distinguished by [isYes]/[isNo]/[isHalt] (and matched
 * exhaustively via [whenIs]):
 * - [Yes] -- the goal succeeded, with [solvedQuery] and a [Substitution.Unifier] as [substitution];
 * - [No] -- the goal failed, [substitution] is [Substitution.Fail] and [solvedQuery] is `null`;
 * - [Halt] -- resolution was aborted by a [ResolutionException], carried in [exception].
 *
 * Construct instances via the companion's [yes], [no], [halt] factories.
 */
sealed interface Solution :
    Taggable<Solution>,
    Castable<Solution> {
    /** The original goal to which this solution refers */
    @JsName("query")
    val query: Struct

    /** The substitution that has been applied to find the solution, or a failed substitution */
    @JsName("substitution")
    val substitution: Substitution

    /** The exception that made resolution halt, if this is a [Halt] solution, or `null` otherwise */
    @JsName("exception")
    val exception: ResolutionException?

    /** The [Struct] representing the solution, or `null` in case of a non-successful solution */
    @JsName("solvedQuery")
    val solvedQuery: Struct?

    /** Whether this is a [Yes] (successful) solution */
    @JsName("isYes")
    val isYes: Boolean

    /** Whether this is a [No] (failed) solution */
    @JsName("isNo")
    val isNo: Boolean

    /** Whether this is a [Halt] (aborted by an exception) solution */
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

    /**
     * Exhaustively (or partially) pattern-matches this [Solution] against its three possible subtypes, invoking
     * whichever of [yes]/[no]/[halt] corresponds to this solution's actual kind, or [otherwise] if that branch was
     * left `null` (by default, throwing [IllegalStateException]).
     */
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

    /**
     * Returns a [Solution] equivalent to this one, but with every variable not occurring in [query] (nor bound, in
     * [substitution], to a variable occurring in [query]) removed from [substitution]. Useful to drop
     * resolution-internal variables before presenting a solution to a user.
     */
    @JsName("cleanUp")
    fun cleanUp(): Solution

    /** Returns the [Term] bound to [variable] in [substitution], or `null` if [variable] is unbound (or this is not a [Yes] solution). */
    @JsName("valueOf")
    fun valueOf(variable: Var): Term?

    /** Same as [valueOf], but looking [variable] up by name rather than by [Var] instance. */
    @JsName("valueOfByName")
    fun valueOf(variable: String): Term?

    /** A type representing the successful solution */
    sealed interface Yes : Solution {
        /** The [Substitution.Unifier] that made [query] succeed. */
        override val substitution: Substitution.Unifier

        /** The [query], with [substitution] applied to it. */
        override val solvedQuery: Struct

        override fun replaceTags(tags: Map<String, Any>): Yes

        /** Returns a [Yes] solution identical to this one, except for the explicitly-provided arguments. */
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
        /** Always [Substitution.Fail], since the goal did not succeed. */
        override val substitution: Substitution.Fail

        /** Always `null`, since the goal did not succeed. */
        override val solvedQuery: Nothing?

        override fun replaceTags(tags: Map<String, Any>): No

        /** Returns a [No] solution identical to this one, except for the explicitly-provided [query]. */
        @JsName("copy")
        fun copy(query: Struct = this.query): No

        override fun cleanUp(): No

        override fun asNo(): No = this
    }

    /** A type representing a failed (halted) solution because of an exception */
    sealed interface Halt : Solution {
        /** The exception that made resolution halt. */
        override val exception: ResolutionException

        override fun replaceTags(tags: Map<String, Any>): Halt

        /** Returns a [Halt] solution identical to this one, except for the explicitly-provided arguments. */
        @JsName("copy")
        fun copy(
            query: Struct = this.query,
            exception: ResolutionException = this.exception,
        ): Halt

        override fun cleanUp(): Halt

        override fun asHalt(): Halt = this
    }

    companion object {
        /** Creates a successful ([Yes]) solution for [query], applying [substitution] (empty by default). */
        @JvmStatic
        @JsName("yes")
        fun yes(
            query: Struct,
            substitution: Substitution.Unifier = Substitution.empty(),
        ): Yes = SolutionImpl.YesImpl(query, substitution)

        /** Same as [yes], but building the query out of [signature] and [arguments]. */
        @JvmStatic
        @JsName("yesBySignature")
        fun yes(
            signature: Signature,
            arguments: List<Term>,
            substitution: Substitution.Unifier = Substitution.empty(),
        ): Yes = SolutionImpl.YesImpl(signature, arguments, substitution)

        /** Creates a failed ([No]) solution for [query]. */
        @JvmStatic
        @JsName("no")
        fun no(query: Struct): No = SolutionImpl.NoImpl(query)

        /** Same as [no], but building the query out of [signature] and [arguments]. */
        @JvmStatic
        @JsName("noBySignature")
        fun no(
            signature: Signature,
            arguments: List<Term>,
        ): No = SolutionImpl.NoImpl(signature, arguments)

        /** Creates an aborted ([Halt]) solution for [query], carrying [exception]. */
        @JvmStatic
        @JsName("halt")
        fun halt(
            query: Struct,
            exception: ResolutionException,
        ): Halt = SolutionImpl.HaltImpl(query, exception)

        /** Same as [halt], but building the query out of [signature] and [arguments]. */
        @JvmStatic
        @JsName("haltBySignature")
        fun halt(
            signature: Signature,
            arguments: List<Term>,
            exception: ResolutionException,
        ): Halt = SolutionImpl.HaltImpl(signature, arguments, exception)
    }
}
