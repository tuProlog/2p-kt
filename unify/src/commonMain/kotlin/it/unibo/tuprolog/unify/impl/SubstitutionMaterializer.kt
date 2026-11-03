package it.unibo.tuprolog.unify.impl

import it.unibo.tuprolog.core.Cons
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var

/**
 * Turns triangular [Bindings] into the public, idempotent [Substitution], resolving each bound variable once.
 *
 * Cyclic bindings (only possible with occurs-check disabled) are expanded until a variable already being expanded is
 * met, which is left in place: e.g. `X -> f(X)` stays `X -> f(X)`.
 */
internal class SubstitutionMaterializer(
    private val bindings: Bindings,
) {
    private val resolved = HashMap<Var, Term>()
    private val expanding = HashSet<Var>()

    fun materialize(): Substitution {
        val variables = ArrayList<Var>()
        bindings.forEachVariable { variables.add(it) }
        if (variables.isEmpty()) return Substitution.empty()
        // shortens variable-to-variable chains to one hop, so that long chains need no deep recursion below
        for (variable in variables) bindings.representative(variable)
        val result = LinkedHashMap<Var, Term>()
        for (variable in variables) result[variable] = resolveVariable(variable)
        return Substitution.unifier(result)
    }

    private fun resolveVariable(variable: Var): Term {
        resolved[variable]?.let { return it }
        val value = bindings[variable]
        if (value == null || !expanding.add(variable)) return variable
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
