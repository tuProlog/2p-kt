package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var

/** A [BindingStore] over a map. */
internal class HashMapStore : BindingStore {
    private val bindings = HashMap<Var, Term>()

    private val functorVariables = HashSet<Var>()

    override fun isFunctorVariable(variable: Var): Boolean = variable in functorVariables

    override fun constrainFunctor(variable: Var) {
        functorVariables.add(variable)
    }

    override fun get(variable: Var): Term? = bindings[variable]

    override fun bind(
        variable: Var,
        term: Term,
    ) {
        bindings[variable] = term
    }
}
