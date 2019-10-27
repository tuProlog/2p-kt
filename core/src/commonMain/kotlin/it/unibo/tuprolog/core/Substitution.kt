package it.unibo.tuprolog.core

/**
 * An interface representing a mapping between Variables and their Term substitutions
 *
 * @author Enrico
 * @author Giovanni
 */
sealed class Substitution : Map<Var, Term> {

    /** Whether this Substitution is a successful one (is a Unifier) */
    open val isSuccess: Boolean = false
    /** Whether this Substitution is a failed one */
    open val isFailed: Boolean = false

    /** Creates a new Successful Substitution (aka Unifier) with given mappings (after some checks) */
    class Unifier(mappings: Map<Var, Term>) : Substitution(),
            Map<Var, Term> by (mappings.trimVariableChains().withoutIdentityMappings()) {

        // NOTE: no check for contradictions is made upon object construction
        // because a map cannot have a mapping from same key to more than one
        // different value, by definition of map type

        override val isSuccess: Boolean = true

        /** The mappings used to implement [equals], [hashCode] and [toString] */
        // this should be kept in sync with class "by" right expression
        // for now (27/10/2019) there's no more concise way to do this, because accessing directly the delegated object is not possible
        private val delegatedMappings by lazy { mappings.trimVariableChains().withoutIdentityMappings() }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Unifier

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
    }

    /** The Failed Substitution instance */
    object Fail : Substitution(), Map<Var, Term> by emptyMap() {
        override val isFailed: Boolean = true
    }

    /** Applies the Substitution to the given Term */
    fun applyTo(term: Term): Term = term[this]

    /**
     * Creates a new substitution that's the *composition* of this and [other]
     *
     * However additional checks are performed:
     * - If one of operands is [Substitution.Fail], the result is [Substitution.Fail]
     * - If the set of substitutions resulting from this union is contradicting, the result is [Substitution.Fail]
     */
    operator fun plus(other: Substitution): Substitution = when {
        anyFailed(this, other) || anyContradiction(this, other) -> Fail
        else -> (this as Map<Var, Term> + other).asUnifier()
    }

    /**
     * Returns a substitution containing all entries of the original substitution except those
     * entries which variable keys are contained in the given [other] substitution.
     */
    operator fun minus(other: Substitution): Substitution = when (this) {
        is Fail -> Fail
        else -> (this as Map<Var, Term> - other.keys).asUnifier()
    }

    /** Substitution companion with factory functionality */
    companion object {

        /** Returns failed substitution instance */
        fun failed(): Fail = Fail

        /** Returns empty successful substitution (aka Unifier) instance */
        fun empty(): Unifier = emptyMap<Var, Term>().asUnifier()

        /** Conversion from a raw Map<Var, Term> to Successful Substitution (aka Unifier) type */
        fun Map<Var, Term>.asUnifier(): Unifier = Unifier(this)

        /** Creates a Substitution of given Variable with given Term */
        fun of(variable: Var, withTerm: Term): Unifier = of(variable to withTerm) as Unifier

        /** Creates a Substitution from the new Variable, with given name, to given Term */
        fun of(variable: String, withTerm: Term): Unifier = of(Var.of(variable) to withTerm) as Unifier

        /** Crates a Substitution from given substitution pairs; if any contradiction is found, the result will be [Substitution.Fail] */
        fun of(substitutionPair: Pair<Var, Term>, vararg substitutionPairs: Pair<Var, Term>): Substitution = when {
            anyContradiction(substitutionPairs.asSequence() + substitutionPair) -> Fail
            else -> mapOf(substitutionPair, *substitutionPairs).asUnifier()
        }

        /** Crates a Substitution from given substitution pairs; if any contradiction is found, the result will be [Substitution.Fail] */
        fun of(substitutionPairs: Iterable<Pair<Var, Term>>): Substitution = when {
            anyContradiction(substitutionPairs.asSequence()) -> Fail
            else -> substitutionPairs.toMap().asUnifier()
        }

        /** Creates a new Substitution from given substitutions; if any failure or contradiction is found, the result will be [Substitution.Fail] */
        fun of(substitution: Substitution, vararg substitutions: Substitution): Substitution =
                substitutions.fold(substitution, Substitution::plus)

        /** Utility function to check if any of provided Substitution is failed */
        private fun anyFailed(vararg substitution: Substitution): Boolean = substitution.any { it.isFailed }

        /**
         * Utility function to check if there's any contradiction in provided substitutions
         *
         * Computational Complexity: length of the smaller among provided substitutions
         */
        private fun anyContradiction(substitution: Substitution, other: Substitution): Boolean =
                when {
                    substitution.count() < other.count() -> substitution to other
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
                count() < 2 -> this
                else -> this.mapValues { (varKey, term) ->
                    term.takeIf { it !is Var } ?: varKey.trimVariableChain(this)
                }
            }
        }

        /** Utility function to filter out identity mappings from a Map<Var, Term> */
        private fun Map<Var, Term>.withoutIdentityMappings(): Map<Var, Term> =
                filterNot { (`var`, term) -> `var` == term }
    }
}
