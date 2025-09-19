package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.Substitution.Unifier
import it.unibo.tuprolog.utils.TagsOperator
import kotlin.collections.Collection as KtCollection

internal sealed class SubstitutionImpl : Substitution {
    override val isSuccess: Boolean
        get() = false

    override val isFailed: Boolean
        get() = false

    abstract override fun getOriginal(variable: Var): Var?

    override fun plus(other: Substitution): Substitution = plus(other) { x, y -> x + y }

    override fun plus(
        other: Substitution,
        tagsMerger: TagsOperator,
    ): Substitution =
        when {
            anyFailed(this, other) || anyContradiction(this, other) -> {
                FailImpl(tagsMerger(tags, other.tags))
            }
            else -> {
                UnifierImpl.of(
                    this.mapValues { (_, value) -> value.apply(other) } + other,
                    tagsMerger(tags, other.tags),
                )
            }
        }

    override operator fun minus(keys: Iterable<Var>): Substitution {
        val thiz: Map<Var, Term> = this
        return whenIs(
            unifier = { UnifierImpl.of(thiz - keys, tags) },
            fail = { FailImpl(tags) },
        )
    }

    override fun minus(variable: Var): Substitution = minus(listOf(variable))

    override fun minus(
        variable: Var,
        vararg otherVariables: Var,
    ): Substitution = minus(listOf(variable, *otherVariables))

    override operator fun minus(other: Substitution): Substitution = this - other.keys

    override fun filter(predicate: (Map.Entry<Var, Term>) -> Boolean): Substitution {
        val thiz: Map<Var, Term> = this
        return whenIs(
            unifier = { UnifierImpl.of(thiz.filter(predicate), tags) },
            fail = { FailImpl(tags) },
        )
    }

    override fun filter(variables: KtCollection<Var>): Substitution = filter { k, _ -> k in variables }

    override fun filter(predicate: (key: Var, value: Term) -> Boolean): Substitution =
        filter { (key, value) ->
            predicate(
                key,
                value,
            )
        }

    /** Creates a new Successful Substitution (aka Unifier) with given mappings (after some checks) */
    class UnifierImpl private constructor(
        private val assignments: Map<Var, Term>,
        override val tags: Map<String, Any>,
    ) : SubstitutionImpl(),
        Unifier,
        Map<Var, Term> by (assignments) {
        companion object {
            fun of(
                mappings: Map<Var, Term>,
                tags: Map<String, Any> = emptyMap(),
            ) = UnifierImpl(mappings.trimVariableChains().withoutIdentityMappings(), tags)
        }

        // NOTE: no check for contradictions is made upon object construction
        // because a map cannot have a mapping from same key to more than one
        // different value, by definition of map type

        private fun reverseLookUp(variable: Var): Var? =
            entries
                .filter { it.value == variable }
                .map { it.key }
                .firstOrNull()

        override fun getOriginal(variable: Var): Var? =
            sequence<Var> {
                var current: Var? = reverseLookUp(variable)
                while (current != null) {
                    yield(current)
                    current = reverseLookUp(current)
                }
            }.lastOrNull()

        override val isSuccess: Boolean
            get() = true

        override fun minus(keys: Iterable<Var>): Unifier = super.minus(keys).castToUnifier()

        override fun minus(other: Substitution): Unifier = super.minus(other).castToUnifier()

        override fun minus(variable: Var): Unifier = super.minus(variable).castToUnifier()

        override fun minus(
            variable: Var,
            vararg otherVariables: Var,
        ): Unifier = super.minus(variable, *otherVariables).castToUnifier()

        override fun filter(predicate: (Map.Entry<Var, Term>) -> Boolean): Unifier =
            super
                .filter(
                    predicate,
                ).castToUnifier()

        override fun filter(predicate: (key: Var, value: Term) -> Boolean): Unifier =
            super
                .filter(
                    predicate,
                ).castToUnifier()

        override fun filter(variables: KtCollection<Var>): Unifier = super.filter(variables).castToUnifier()

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as UnifierImpl

            if (isSuccess != other.isSuccess) return false
            if (assignments != other.assignments) return false

            return true
        }

        override fun hashCode(): Int = assignments.hashCode()

        override fun toString(): String = assignments.toString()

        override fun applyTo(term: Term): Term = term.apply(this)

        override fun replaceTags(tags: Map<String, Any>): Unifier =
            if (tags == this.tags) {
                this
            } else {
                UnifierImpl(
                    assignments,
                    tags,
                )
            }
    }

    /** The Failed Substitution instance */
    class FailImpl constructor(
        override val tags: Map<String, Any> = emptyMap(),
    ) : SubstitutionImpl(),
        Substitution.Fail,
        Map<Var, Term> by emptyMap() {
        override val isFailed: Boolean
            get() = true

        override fun getOriginal(variable: Var): Nothing? = null

        override fun minus(other: Substitution): FailImpl = this

        override fun minus(keys: Iterable<Var>): FailImpl = this

        override fun minus(variable: Var): FailImpl = this

        override fun minus(
            variable: Var,
            vararg otherVariables: Var,
        ): FailImpl = this

        override fun filter(predicate: (Map.Entry<Var, Term>) -> Boolean): FailImpl = this

        override fun filter(predicate: (key: Var, value: Term) -> Boolean): FailImpl = this

        override fun filter(variables: KtCollection<Var>): FailImpl = this

        override fun toString(): String = "{Failed Substitution}"

        override fun applyTo(term: Term): Nothing? = null

        override fun replaceTags(tags: Map<String, Any>): FailImpl = if (tags == this.tags) this else FailImpl(tags)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as FailImpl

            return isFailed == other.isFailed
        }

        override fun hashCode(): Int = emptyMap<Var, Term>().hashCode()
    }

    /** Substitution companion with factory functionality */
    companion object {
        /** Crates a Substitution from given substitution pairs; if any contradiction is found, the result will be [Substitution.Fail] */
        fun of(substitutionPairs: Sequence<Pair<Var, Term>>): Substitution =
            when {
                anyContradiction(substitutionPairs) -> FailImpl()
                else -> UnifierImpl.of(substitutionPairs.toMap())
            }

        /** Utility function to check if any of provided Substitution is failed */
        private fun anyFailed(vararg substitution: Substitution): Boolean = substitution.any { it.isFailed }

        /**
         * Utility function to check if there's any contradiction in provided substitutions
         *
         * Computational Complexity: length of the smaller among provided substitutions
         */
        private fun anyContradiction(
            substitution: Substitution,
            other: Substitution,
        ): Boolean =
            when {
                substitution.size < other.size -> substitution to other
                else -> other to substitution
            }.let { (smaller, bigger) ->
                smaller.any { (`var`, substitution) ->
                    // if var is present and different, contradiction is present
                    bigger[`var`]?.let { it != substitution } ?: false
                }
            }

        /**
         * Utility function to check if there's any contradiction in provided substitution pairs
         *
         * Computational Complexity: length of the entire sequence (if no contradiction found)
         */
        private fun anyContradiction(substitutionPairs: Sequence<Pair<Var, Term>>): Boolean =
            when {
                substitutionPairs.none() -> false // no pair, no contradiction
                with(substitutionPairs.iterator()) {
                    next()
                    !hasNext()
                } -> false // one pair, no contradiction
                else ->
                    mutableMapOf<Var, Term>().let { alreadySeenSubstitutions ->
                        substitutionPairs.forEach { (variable, substitution) ->
                            when (val alreadyPresent = alreadySeenSubstitutions[variable]) {
                                null -> alreadySeenSubstitutions[variable] = substitution
                                else -> if (alreadyPresent != substitution) return@let true // contradiction found
                            }
                        }
                        false
                    }
            }

        /** Utility function to trim all Map variable chains, i.e. all var keys will be bound to the last possible term */
        private fun Map<Var, Term>.trimVariableChains(): Map<Var, Term> {
            /** Utility function to trim a single variable chain against a provided map, returning the last term */
            fun Var.trimVariableChain(mappings: Map<Var, Term>): Term {
                val alreadyUsedKeys = mutableSetOf(this) // to prevent infinite loop
                var current: Term = mappings.getValue(this)
                while (current.isVar && current in mappings && current !in alreadyUsedKeys) {
                    current.castToVar().let {
                        alreadyUsedKeys += it
                        current = mappings.getValue(it)
                    }
                }
                return current
            }

            return when {
                size < 2 -> this
                else ->
                    this.mapValues { (varKey, term) ->
                        term.takeIf { !it.isVar } ?: varKey.trimVariableChain(this)
                    }
            }
        }

        /** Utility function to filter out identity mappings from a Map<Var, Term> */
        private fun Map<Var, Term>.withoutIdentityMappings(): Map<Var, Term> =
            filterNot { (`var`, term) ->
                `var` == term
            }
    }
}
