package it.unibo.tuprolog.unify.impl

import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Pattern
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.withFunctorVariables

/**
 * Turns triangular [Bindings] into the public, idempotent [Substitution], resolving each bound variable once.
 *
 * Only the bindings established during the run get resolved: the ones of the (idempotent) base just get them applied,
 * through [Substitution.plus], which is lazy on long lists. Cyclic bindings (only possible with occurs-check disabled)
 * are expanded until a variable already being expanded is met, which is left in place: e.g. `X -> f(X)` stays as is.
 */
internal class SubstitutionMaterializer(
    private val bindings: Bindings,
) {
    private val resolved = HashMap<Var, Term>()
    private val expanding = HashSet<Var>()

    fun materialize(): Substitution = materializeBindings().withFunctorVariables(bindings.functorVariables)

    private fun materializeBindings(): Substitution {
        val base = bindings.base
        val result = LinkedHashMap<Var, Term>()
        bindings.forEachNew { variable, term -> result[variable] = term }
        if (result.isEmpty()) return base
        // a single binding has nothing to resolve: at most, its value contains the variable itself, which stays
        if (base.isEmpty() && result.size == 1) return Substitution.unifier(result)
        // shortens variable-to-variable chains to one hop, so that long chains need no deep recursion below
        for ((variable, term) in result) if (term is Var) bindings.representative(variable)
        for (entry in result.entries) entry.setValue(resolveVariable(entry.key))
        val resolvedBindings = Substitution.unifier(result)
        return if (base.isEmpty()) resolvedBindings else base + resolvedBindings
    }

    private fun resolveVariable(variable: Var): Term {
        resolved[variable]?.let { return it }
        val value = bindings[variable] ?: return variable
        if (value !is Var && value.isGround) return value
        if (!expanding.add(variable)) return variable
        val result = resolve(value)
        expanding.remove(variable)
        resolved[variable] = result
        return result
    }

    private fun resolve(term: Term): Term =
        when {
            term is Var -> resolveVariable(term)
            term.isGround -> term
            term is Cons -> resolveList(term)
            term is Struct -> resolveStruct(term)
            term is Pattern -> resolvePattern(term)
            else -> term
        }

    private fun resolveStruct(struct: Struct): Term {
        var args: MutableList<Term>? = null
        for (i in 0 until struct.arity) {
            val arg = struct[i]
            val resolvedArg = resolve(arg)
            if (resolvedArg !== arg) {
                val newArgs = args ?: struct.args.toMutableList().also { args = it }
                newArgs[i] = resolvedArg
            }
        }
        return args?.let { Struct.of(struct.functor, it).withTagsOf(struct) } ?: struct
    }

    private fun resolvePattern(pattern: Pattern): Term {
        val functor = resolve(pattern.functor)
        val args = pattern.args.map { resolve(it) }
        if (functor === pattern.functor && args.indices.all { args[it] === pattern.args[it] }) return pattern
        // functor variables are only ever bound to atoms or variables
        val resolved = if (functor is Var) Pattern.of(functor, args) else Struct.of(functor.castToAtom().value, args)
        return resolved.withTagsOf(pattern)
    }

    /** Iterates over the spine of [list], so that long lists need no deep recursion. */
    private fun resolveList(list: Cons): Term {
        val cells = ArrayList<Cons>()
        val heads = ArrayList<Term>()
        var changed = false
        var current: Term = list
        while (current is Cons) {
            val head = resolve(current.head)
            changed = changed || head !== current.head
            cells.add(current)
            heads.add(head)
            current = current.tail
        }
        var result = resolve(current)
        if (!changed && result === current) return list
        for (i in cells.lastIndex downTo 0) {
            result = Cons.of(heads[i], result).withTagsOf(cells[i])
        }
        return result
    }

    private fun Term.withTagsOf(original: Term): Term =
        if (original.tags.isEmpty()) this else replaceTags(original.tags)
}
