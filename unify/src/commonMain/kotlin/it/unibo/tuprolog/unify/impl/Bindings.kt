package it.unibo.tuprolog.unify.impl

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.BindingStore
import it.unibo.tuprolog.unify.functorVariables

/**
 * The variable bindings of one unification run: the bindings established during the run, layered over [base] ones
 * (the context, or the first operand of a merge) which are never copied. Bindings are *triangular*: a bound term may
 * contain bound variables, which are only resolved when needed. The [base] is assumed to be idempotent.
 *
 * With a [store], the run's bindings go there instead (#1010): variables are only ever bound, never rebound, so chains
 * are not compressed, and there are no new bindings to enumerate.
 */
internal class Bindings(
    val base: Substitution,
    private val store: BindingStore? = null,
) {
    private var local: MutableMap<Var, Term>? = null

    private val baseFunctorVariables: Set<Var> = base.functorVariables

    private var localFunctorVariables: MutableSet<Var>? = null

    /**
     * Whether the unbound representative [variable] is a functor variable, i.e. it can only be bound to atoms or
     * variables. Functor variables are tracked as representatives: see [UnificationRun].
     */
    fun isFunctorVariable(variable: Var): Boolean =
        variable in baseFunctorVariables ||
            (
                if (store !=
                    null
                ) {
                    store.isFunctorVariable(variable)
                } else {
                    localFunctorVariables?.contains(variable) == true
                }
            )

    /** Makes the unbound representative [variable] a functor variable. */
    fun constrainFunctor(variable: Var) {
        if (isFunctorVariable(variable)) return
        if (store != null) return store.constrainFunctor(variable)
        (localFunctorVariables ?: HashSet<Var>().also { localFunctorVariables = it }).add(variable)
    }

    /** The functor variables of [base], plus the ones made such during the run (unless they go to a store). */
    val functorVariables: Set<Var>
        get() = localFunctorVariables?.let { baseFunctorVariables + it } ?: baseFunctorVariables

    operator fun get(variable: Var): Term? =
        (if (store != null) store[variable] else local?.get(variable)) ?: base[variable]

    operator fun set(
        variable: Var,
        term: Term,
    ) {
        if (store != null) return store.bind(variable, term)
        val map = local ?: LinkedHashMap<Var, Term>().also { local = it }
        map[variable] = term
    }

    /** The bindings established during the run, of variables not bound by [base], in order. */
    fun forEachNew(action: (Var, Term) -> Unit) {
        local?.forEach { (variable, term) -> if (variable !in base) action(variable, term) }
    }

    /**
     * The last variable of the variable-to-variable chain starting at [term], i.e. a variable that is either unbound or
     * bound to a non-variable term; [term] itself if it is not a variable. Compresses the chain along the way.
     */
    fun representative(term: Term): Term {
        if (term !is Var) return term
        var current: Var = term
        var hops = 0
        while (true) {
            val next = this[current]
            if (next !is Var) break
            current = next
            hops++
        }
        if (hops > 1 && store == null) compress(term, current)
        return current
    }

    private fun compress(
        from: Var,
        to: Var,
    ) {
        var current: Var = from
        while (current != to) {
            val next = this[current] as Var
            this[current] = to
            current = next
        }
    }

    /** The term [term] currently stands for: the value of its [representative] if bound, the representative otherwise. */
    fun dereference(term: Term): Term = valueOf(representative(term))

    /** The value of [representative] if it is a bound variable, [representative] itself otherwise. */
    fun valueOf(representative: Term): Term = (representative as? Var)?.let { this[it] } ?: representative
}
