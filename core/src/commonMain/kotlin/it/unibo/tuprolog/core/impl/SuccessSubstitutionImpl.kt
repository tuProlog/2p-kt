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
    : Substitution, Map<Var, Term> by _mappings
