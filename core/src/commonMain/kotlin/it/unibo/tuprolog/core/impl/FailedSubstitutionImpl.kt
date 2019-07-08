package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var

/**
 * Implementation for a failed substitution, with no mappings
 *
 * @author Enrico
 */
internal object FailedSubstitutionImpl : Substitution, Map<Var, Term> by emptyMap() {
    override val isFailed = true
    override val isSuccess = false
}
