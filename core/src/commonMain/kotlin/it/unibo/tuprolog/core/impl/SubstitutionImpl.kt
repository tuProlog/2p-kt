package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.utils.setTags
import kotlin.collections.Collection as KtCollection

internal sealed class SubstitutionImpl : Substitution {

    override val isSuccess: Boolean
        get() = false

    override val isFailed: Boolean
        get() = false

    abstract override fun getOriginal(variable: Var): Var?

    override operator fun plus(other: Substitution): SubstitutionImpl = when {
        anyFailed(this, other) || anyContradiction(this, other) -> FailImpl
        else -> UnifierImpl(this.mapValues { (_, value) -> value.apply(other) } + other)
    }

    override operator fun minus(keys: Iterable<Var>): SubstitutionImpl = when (this) {
        is FailImpl -> FailImpl
        else -> UnifierImpl(this as Map<Var, Term> - keys)
    }

    override operator fun minus(other: Substitution): SubstitutionImpl = this - other.keys

    override fun filter(predicate: (Map.Entry<Var, Term>) -> Boolean): SubstitutionImpl = when (this) {
        is FailImpl -> FailImpl
        else -> UnifierImpl((this as Map<Var, Term>).filter(predicate))
    }

    override fun filter(variables: KtCollection<Var>): SubstitutionImpl = filter { k, _ -> k in variables }

    override fun filter(predicate: (key: Var, value: Term) -> Boolean): SubstitutionImpl =
        filter { (key, value) -> predicate(key, value) }

    /** Creates a new Successful Substitution (aka Unifier) with given mappings (after some checks) */
    class UnifierImpl(mappings: Map<Var, Term>) :
        SubstitutionImpl(),
        Substitution.Unifier,
        Map<Var, Term> by (mappings.trimVariableChains().withoutIdentityMappings()) {

        // NOTE: no check for contradictions is made upon object construction
        // because a map cannot have a mapping from same key to more than one
        // different value, by definition of map type

        private fun reverseLookUp(variable: Var): Var? {
            return entries
                .filter { it.value == variable }
                .map { it.key }
                .firstOrNull()
        }

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

        override fun minus(keys: Iterable<Var>): UnifierImpl = super.minus(keys) as UnifierImpl

        override fun minus(other: Substitution): UnifierImpl = super.minus(other) as UnifierImpl

        override fun filter(predicate: (Map.Entry<Var, Term>) -> Boolean): UnifierImpl =
            super.filter(predicate) as UnifierImpl

        override fun filter(predicate: (key: Var, value: Term) -> Boolean): UnifierImpl =
            super.filter(predicate) as UnifierImpl

        override fun filter(variables: KtCollection<Var>): UnifierImpl = super.filter(variables) as UnifierImpl

        /** The mappings used to implement [equals], [hashCode] and [toString] */
        // this should be kept in sync with class "by" right expression
        // for now (27/10/2019) there's no more concise way to do this, because accessing directly the delegated object is not possible
        private val delegatedMappings by lazy { mappings.trimVariableChains().withoutIdentityMappings() }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as UnifierImpl

            if (delegatedMappings != other.delegatedMappings) return false
            if (isSuccess != other.isSuccess) return false

            return true
        }

        override fun hashCode(): Int {
            var result = delegatedMappings.hashCode()
            result = 31 * result + isSuccess.hashCode()
            return result
        }

        override fun toString(): String = delegatedMappings.toString()

        override fun applyTo(term: Term): Term =
            when {
                isEmpty() || term.isGround -> term
                term is Var -> this[term] ?: term
                term is Struct -> Struct.of(term.functor, term.argsList.map { applyTo(it) }).setTags(term.tags)
                else -> term
            }
    }

    /** The Failed Substitution instance */
    object FailImpl : SubstitutionImpl(), Substitution.Fail, Map<Var, Term> by emptyMap() {
        override val isFailed: Boolean
            get() = true

        override fun getOriginal(variable: Var): Nothing? = null

        override fun minus(other: Substitution): FailImpl = FailImpl

        override fun minus(keys: Iterable<Var>): FailImpl = FailImpl

        override fun filter(predicate: (Map.Entry<Var, Term>) -> Boolean): FailImpl = FailImpl

        override fun filter(predicate: (key: Var, value: Term) -> Boolean): FailImpl = FailImpl

        override fun filter(variables: KtCollection<Var>): FailImpl = FailImpl

        override fun toString(): String = "{Failed Substitution}"

        override fun applyTo(term: Term): Nothing? = null
    }

    /** Substitution companion with factory functionality */
    companion object {

        /** Crates a Substitution from given substitution pairs; if any contradiction is found, the result will be [Substitution.Fail] */
        fun of(substitutionPairs: Sequence<Pair<Var, Term>>): Substitution = when {
            anyContradiction(substitutionPairs) -> FailImpl
            else -> UnifierImpl(substitutionPairs.toMap())
        }

        /** Utility function to check if any of provided Substitution is failed */
        private fun anyFailed(vararg substitution: Substitution): Boolean = substitution.any { it.isFailed }

        /**
         * Utility function to check if there's any contradiction in provided substitutions
         *
         * Computational Complexity: length of the smaller among provided substitutions
         */
        private fun anyContradiction(substitution: Substitution, other: Substitution): Boolean =
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
                with(substitutionPairs.iterator()) { next(); !hasNext() } -> false // one pair, no contradiction
                else ->
                    mutableMapOf<Var, Term>().let { alreadySeenSubstitutions ->
                        substitutionPairs.forEach { (`var`, substitution) ->
                            when (val alreadyPresent = alreadySeenSubstitutions[`var`]) {
                                null -> alreadySeenSubstitutions[`var`] = substitution
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
                while (current is Var && current in mappings && current !in alreadyUsedKeys) {
                    alreadyUsedKeys += current
                    current = mappings.getValue(current)
                }
                return current
            }

            return when {
                size < 2 -> this
                else ->
                    this.mapValues { (varKey, term) ->
                        term.takeIf { it !is Var } ?: varKey.trimVariableChain(this)
                    }
            }
        }

        /** Utility function to filter out identity mappings from a Map<Var, Term> */
        private fun Map<Var, Term>.withoutIdentityMappings(): Map<Var, Term> =
            filterNot { (`var`, term) -> `var` == term }
    }
}
