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

    /** Creates a new Successful Substitution (aka Unifier) with given mappings */
    data class Unifier(private val mappings: Map<Var, Term>) : Substitution(), Map<Var, Term> by mappings {
        override val isSuccess: Boolean = true
    }

    /** The Failed Substitution instance */
    object Fail : Substitution(), Map<Var, Term> by emptyMap() {
        override val isFailed: Boolean = true
    }

    /** Applies the Substitution to the given Term */
    fun applyTo(term: Term): Term = term[this]

    /** Substitution companion with factory functionality */
    companion object {

        /** Returns failed substitution instance */
        fun failed(): Fail = Fail

        /** Returns empty successful substitution (aka Unifier) instance */
        fun empty(): Unifier = emptyMap<Var, Term>().asUnifier()

        /** Conversion from a raw Map<Var, Term> to Successful Substitution (aka Unifier) type */
        fun Map<Var, Term>.asUnifier(): Unifier = Unifier(this)

        /** Creates a Substitution of given Variable with given Term */
        fun of(variable: Var, withTerm: Term): Unifier = of(variable to withTerm)

        /** Creates a Substitution from the new Variable, with given name, to given Term */
        fun of(variable: String, withTerm: Term): Unifier = of(Var.of(variable) to withTerm)

        /** Crates a Substitution from given substitution pairs */
        fun of(substitutionPair: Pair<Var, Term>, vararg substitutionPairs: Pair<Var, Term>): Unifier =
                mapOf(substitutionPair, *substitutionPairs).asUnifier()

        /** Crates a Substitution from given substitution pairs */
        fun of(substitutionPairs: Iterable<Pair<Var, Term>>): Unifier =
                substitutionPairs.toMap().asUnifier()

        /** Creates a new Substitution from given substitutions */
        fun of(substitution: Substitution, vararg substitutions: Substitution): Unifier =
                substitutions.fold(substitution as Map<Var, Term>) { s1, s2 -> (s1 + s2) }.asUnifier()
    }
}
