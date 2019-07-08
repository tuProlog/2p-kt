package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var

/**
 * Implementation for a successful substitution, with some mappings
 *
 * @author Enrico
 */
internal class SuccessSubstitutionImpl(private val _mappings: Map<Var, Term>)
    : Substitution, Map<Var, Term> by _mappings{

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SuccessSubstitutionImpl

        if (_mappings != other._mappings) return false

        return true
    }

    override fun hashCode(): Int {
        return _mappings.hashCode()
    }
}
