package it.unibo.tuprolog.unify.impl

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var

/**
 * The variable bindings of one unification run: the bindings established during the run, layered over [base] ones
 * (the context, or the first operand of a merge) which are never copied. Bindings are *triangular*: a bound term may
 * contain bound variables, which are only resolved when needed.
 */
internal class Bindings(
    private val base: Map<Var, Term>,
) {
    private var local: MutableMap<Var, Term>? = null

    operator fun get(variable: Var): Term? = local?.get(variable) ?: base[variable]

    operator fun set(
        variable: Var,
        term: Term,
    ) {
        val map = local ?: LinkedHashMap<Var, Term>().also { local = it }
        map[variable] = term
    }

    /** The bound variables: [base]'s first, then the ones bound during the run, each once. */
    fun forEachVariable(action: (Var) -> Unit) {
        base.keys.forEach(action)
        local?.keys?.forEach { if (it !in base) action(it) }
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
        if (hops > 1) compress(term, current)
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
