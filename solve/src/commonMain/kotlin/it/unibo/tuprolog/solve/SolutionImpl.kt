package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Compound
import it.unibo.tuprolog.core.Goal
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.exception.ResolutionException
import kotlin.jvm.JvmStatic

/**
 * A class representing a solution to a goal. It keeps [call] as given, a [Goal] or the [Compound] whose goal is the
 * [query], so that the many solutions of primitives (for their [Struct] calls) wrap them only when [query] is read.
 */
internal sealed class SolutionImpl(
    protected val call: Compound,
    override val tags: Map<String, Any>,
) : Solution {
    override val query: Goal
        get() = call.toGoal()

    // what the query asks to solve, the same for a call and for its goal
    private val body: Term
        get() = if (call is Goal) call.body else call

    override val isYes: Boolean
        get() = false

    override val isNo: Boolean
        get() = false

    override val isHalt: Boolean
        get() = false

    override val exception: ResolutionException?
        get() = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SolutionImpl

        if (body != other.body) return false
        if (substitution != other.substitution) return false

        return true
    }

    override fun hashCode(): Int {
        var result = body.hashCode()
        result = 31 * result + substitution.hashCode()
        return result
    }

    override fun valueOf(variable: Var): Term? = (substitution as? Substitution.Unifier)?.get(variable)

    override fun valueOf(variable: String): Term? = (substitution as? Substitution.Unifier)?.getByName(variable)

    /** A class representing the successful solution */
    class YesImpl(
        query: Compound,
        /** The successful substitution applied finding the solution */
        override val substitution: Substitution.Unifier = Substitution.empty(),
        tags: Map<String, Any> = emptyMap(),
    ) : SolutionImpl(query, tags),
        Solution.Yes {
        constructor(
            signature: Signature,
            arguments: List<Term>,
            substitution: Substitution.Unifier = Substitution.empty(),
            tags: Map<String, Any> = emptyMap(),
        ) : this(signature withArgs arguments, substitution, tags) {
            // a solution always refers to a fully instantiated Struct, that cannot have a vararg Signature
            noVarargSignatureCheck(signature)
        }

        /** The goal representing the solution */
        override val solvedQuery: Goal by lazy { substitution.applyTo(this.query) as Goal }

        override fun replaceTags(tags: Map<String, Any>): YesImpl =
            if (tags === this.tags) {
                this
            } else {
                YesImpl(
                    call,
                    substitution,
                    tags,
                )
            }

        override val isYes: Boolean
            get() = true

        override fun copy(
            query: Compound,
            substitution: Substitution.Unifier,
        ) = YesImpl(query, substitution, tags)

        override fun toString(): String = "Yes(query=$query, substitution=$substitution)"

        override fun cleanUp(hideWildcardVariables: Boolean): Solution.Yes =
            copy(
                substitution =
                    substitution.retaining(
                        call.variables
                            .filterNot {
                                it.isAnonymous || (hideWildcardVariables && it.isWildcard)
                            }.toSet(),
                    ),
            )

        // Named distinctly from cleanUp (rather than an overload on Substitution.Unifier) to avoid any
        // ambiguity with the class's own cleanUp(Boolean) override -- both used to be unambiguous by arity
        // alone (0 vs 1 args) before cleanUp gained its hideWildcardVariables parameter; keeping this a
        // clearly distinct name is simpler than relying on receiver-type disambiguation holding on every
        // Kotlin target.
        private fun Substitution.Unifier.retaining(toRetain: Set<Var>): Substitution.Unifier =
            filter { v, t -> (v in toRetain) || (t is Var && t in toRetain) }
    }

    /** A class representing a failed solution */
    class NoImpl(
        query: Compound,
        tags: Map<String, Any> = emptyMap(),
    ) : SolutionImpl(query, tags),
        Solution.No {
        constructor(
            signature: Signature,
            arguments: List<Term>,
            tags: Map<String, Any> = emptyMap(),
        ) : this(signature withArgs arguments, tags) {
            noVarargSignatureCheck(signature)
        }

        override val substitution: Substitution.Fail
            get() = Substitution.failed()

        override val solvedQuery: Nothing?
            get() = null

        override fun replaceTags(tags: Map<String, Any>): NoImpl = if (tags === this.tags) this else NoImpl(call, tags)

        override val isNo: Boolean
            get() = true

        override fun copy(query: Compound) = NoImpl(query, tags)

        override fun toString(): String = "No(query=$query)"

        override fun cleanUp(hideWildcardVariables: Boolean): Solution.No = this
    }

    /** A class representing a failed (halted) solution because of an exception */
    class HaltImpl(
        query: Compound,
        /** The exception that made the resolution to halt */
        override val exception: ResolutionException,
        tags: Map<String, Any> = emptyMap(),
    ) : SolutionImpl(query, tags),
        Solution.Halt {
        constructor(
            signature: Signature,
            arguments: List<Term>,
            exception: ResolutionException,
            tags: Map<String, Any> = emptyMap(),
        ) : this(signature withArgs arguments, exception, tags) {
            noVarargSignatureCheck(signature)
        }

        override fun replaceTags(tags: Map<String, Any>): HaltImpl =
            if (tags === this.tags) {
                this
            } else {
                HaltImpl(
                    call,
                    exception,
                    tags,
                )
            }

        override fun equals(other: Any?): Boolean {
            if (!super.equals(other)) return false

            other as HaltImpl

            if (exception != other.exception) return false

            return true
        }

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + exception.hashCode()
            return result
        }

        override val substitution: Substitution.Fail
            get() = Substitution.failed()

        override val solvedQuery: Nothing?
            get() = null

        override val isHalt: Boolean
            get() = true

        override fun copy(
            query: Compound,
            exception: ResolutionException,
        ) = HaltImpl(query, exception, tags)

        override fun toString(): String = "Halt(query=$query, exception=$exception)"

        override fun cleanUp(hideWildcardVariables: Boolean): Solution.Halt = this
    }

    protected companion object {
        /** Internal function to check signature validity in constructing Solution instances */
        @JvmStatic
        protected fun noVarargSignatureCheck(signature: Signature) =
            require(!signature.vararg) {
                "The signature should be a well-formed indicator, not vararg `$signature`"
            }
    }
}
