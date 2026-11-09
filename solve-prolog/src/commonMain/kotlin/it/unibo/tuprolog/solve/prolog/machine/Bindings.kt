package it.unibo.tuprolog.solve.prolog.machine

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.Unificator

/**
 * The bindings of a [PrologMachine] (#1010): a mutable map from variables to terms, with a trail recording each binding
 * so that [undo] restores the state at any earlier [mark]. Values may refer to other bound variables: [resolve] follows
 * them.
 *
 * shortcut: every binding is trailed and kept until the resolution ends, and terms are bound with the solver's
 * `Unificator` and copied out by [resolve]; the trailed store of PR 6 (conditional trailing, unification in place,
 * clause environments) replaces this.
 */
internal class Bindings {
    private val values = HashMap<Var, Term>()
    private val trail = ArrayList<Var>()

    /** The current position of the trail, to [undo] to. */
    val mark: Int
        get() = trail.size

    val isEmpty: Boolean
        get() = values.isEmpty()

    /** Binds the unbound [variable] to [value]. */
    fun bind(
        variable: Var,
        value: Term,
    ) {
        values[variable] = value
        trail.add(variable)
    }

    /**
     * Adds the bindings of [unifier]: an unbound variable is bound, a bound one is unified with [unificator]. Returns
     * `false` if a unification fails, leaving the bindings made so far for the caller to [undo].
     */
    fun bindAll(
        unifier: Substitution.Unifier,
        unificator: Unificator,
    ): Boolean {
        for ((variable, value) in unifier) {
            val current = deref(variable)
            if (current is Var) {
                if (current != deref(value)) bind(current, value)
            } else {
                val mgu = unificator.mgu(resolve(current), resolve(value), occurCheckEnabled = false)
                if (mgu !is Substitution.Unifier || !bindAll(mgu, unificator)) return false
            }
        }
        return true
    }

    /** Removes the bindings made since [mark]. */
    fun undo(mark: Int) {
        while (trail.size > mark) values.remove(trail.removeAt(trail.lastIndex))
    }

    /** [term], or the value it is bound to if a variable, following chains of bound variables. */
    fun deref(term: Term): Term {
        var current = term
        while (current is Var) current = values[current] ?: return current
        return current
    }

    /** The bindings of [variables], with their values resolved. */
    fun substitutionOf(variables: Sequence<Var>): Substitution.Unifier {
        if (values.isEmpty()) return Substitution.empty()
        val resolved = HashMap<Var, Term>()
        for (variable in variables) if (variable !in resolved) resolveVar(variable, resolved)
        return Substitution.of(resolved.filter { (variable, value) -> variable != value })
    }

    /** [term] with every bound variable replaced by its value, recursively. */
    fun resolve(term: Term): Term {
        if (values.isEmpty() || term.isGround) return term
        val replacements = substitutionOf(term.variables)
        return if (replacements.isEmpty()) term else term.apply(replacements)
    }

    // post-order without recursion, so that long chains of bindings (e.g. a list built one cell at a time) don't
    // overflow the stack; a variable met again while being resolved (a cyclic binding) is left as is
    private fun resolveVar(
        root: Var,
        resolved: HashMap<Var, Term>,
    ) {
        val stack = ArrayList<Var>()
        val inProgress = HashSet<Var>()
        stack.add(root)
        while (stack.isNotEmpty()) {
            val variable = stack[stack.lastIndex]
            if (variable in resolved) {
                stack.removeAt(stack.lastIndex)
                continue
            }
            val value = deref(variable)
            if (value is Var || value.isGround) {
                resolved[variable] = value
                stack.removeAt(stack.lastIndex)
                continue
            }
            inProgress.add(variable)
            val pending = value.variables.filter { it !in resolved && it !in inProgress }.toList()
            if (pending.isEmpty()) {
                val inner = value.variables.mapNotNull { v -> resolved[v]?.takeIf { it != v }?.let { v to it } }.toMap()
                resolved[variable] = if (inner.isEmpty()) value else value.apply(Substitution.of(inner))
                inProgress.remove(variable)
                stack.removeAt(stack.lastIndex)
            } else {
                stack.addAll(pending)
            }
        }
    }
}
