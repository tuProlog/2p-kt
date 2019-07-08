package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.FailedSubstitutionImpl
import it.unibo.tuprolog.core.impl.SuccessSubstitutionImpl

/**
 * An interface representing a mapping between Variables and their Term substitutions
 *
 * @author Enrico
 */
interface Substitution : Map<Var, Term> {

    /**
     * Whether this Substitution is a failed one
     */
    val isFailed: Boolean

    /**
     * Whether this Substitution is a successful one
     */
    val isSuccess: Boolean

    /**
     * Applies the Substitution to the given Term
     */
    fun ground(term: Term): Term = term[this]

    /**
     * Substitution companion with factory functionality
     */
    companion object {

        /**
         * Returns failed substitution instance
         */
        fun failed(): Substitution = FailedSubstitutionImpl

        /**
         * Returns empty successful substitution instance
         */
        fun empty(): Substitution = emptyMap<Var, Term>().asSuccessSubstitution()

        /**
         * Conversion from a raw Map<Var, Term> to Successful Substitution type
         */
        fun Map<Var, Term>.asSuccessSubstitution(): Substitution =
                SuccessSubstitutionImpl(this)

        /**
         * Creates a Substitution of given Variable with given Term
         */
        fun of(variable: Var, withTerm: Term): Substitution = of(variable to withTerm)

        /**
         * Creates a Substitution of given Variable name with given Term
         */
        fun of(variable: String, withTerm: Term): Substitution = of(Var.of(variable) to withTerm)

        /**
         * Crates a Substitution from given substitution pairs
         */
        fun of(substitutionPair: Pair<Var, Term>, vararg substitutionPairs: Pair<Var, Term>): Substitution =
                mapOf(substitutionPair, *substitutionPairs).asSuccessSubstitution()

        /**
         * Creates a new Substitution from given substitutions
         */
        fun of(substitution: Substitution, vararg substitutions: Substitution): Substitution =
                substitutions.fold(substitution as Map<Var, Term>) { s1, s2 -> (s1 + s2) }.asSuccessSubstitution()
    }
}

// TODO can this be removed?
//fun Array<Substitution>.ground(term: Term): Term {
//    return term[substitutionOf(this[0], *this.sliceArray(1..lastIndex))]
//}
