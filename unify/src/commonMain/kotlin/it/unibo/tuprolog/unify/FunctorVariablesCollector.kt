package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var

/** Collects functor variables (see [functorVariables]), allocating a set only once there is one to hold. */
internal class FunctorVariablesCollector {
    /** The functor variables collected so far, or `null` if there is none. */
    var result: MutableSet<Var>? = null
        private set

    fun add(variables: Collection<Var>) {
        if (variables.isNotEmpty()) set().addAll(variables)
    }

    /** Adds the functors of the patterns in [term]. */
    fun addPatternsIn(term: Term) = forEachPatternFunctor(term) { set().add(it) }

    private fun set(): MutableSet<Var> = result ?: HashSet<Var>().also { result = it }
}
