package it.unibo.tuprolog.solve.prolog.machine

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.BindingStore
import it.unibo.tuprolog.unify.CachedUnificator
import it.unibo.tuprolog.unify.IncrementalUnificator
import it.unibo.tuprolog.unify.Unificator

/**
 * The bindings of a [PrologMachine] (#1010): a mutable map from variables to terms, with a trail recording each binding
 * so that [undo] restores the state at any earlier [mark]. Values may refer to other bound variables: [resolve] follows
 * them.
 *
 * An [IncrementalUnificator] unifies terms right in these bindings; any other `Unificator` computes an mgu of the
 * resolved terms, which is then copied here.
 *
 * shortcut: every binding is trailed and kept until the resolution ends, and terms are copied out by [resolve];
 * conditional trailing and clause environments would avoid both, if profiling shows they matter.
 */
internal class Bindings : BindingStore {
    private val values = HashMap<Var, Term>()
    private val trail = ArrayList<Var>()

    // whether some value holds an opaque term: one that is not ground but shows no variables (e.g. ProbLog's
    // explanations), which only Term.apply can resolve
    private var opaque = false

    /** An independent copy of these bindings and their trail, for a branch of the search of its own (frontier mode). */
    fun copy(): Bindings =
        Bindings().also {
            it.values.putAll(values)
            it.trail.addAll(trail)
            it.opaque = opaque
        }

    /** The current position of the trail, to [undo] to. */
    val mark: Int
        get() = trail.size

    val isEmpty: Boolean
        get() = values.isEmpty()

    override operator fun get(variable: Var): Term? = values[variable]

    /** Binds the unbound [variable] to [term]. */
    override fun bind(
        variable: Var,
        term: Term,
    ) {
        values[variable] = term
        trail.add(variable)
        if (!opaque && !term.isGround) opaque = term.hasOpaqueSubterm()
    }

    /**
     * Unifies [first] and [second] with [unificator], adding the new bindings. Returns `false` if they do not unify,
     * leaving the bindings made so far for the caller to [undo].
     */
    fun unify(
        first: Term,
        second: Term,
        unificator: Unificator,
    ): Boolean {
        unificator.incremental?.let { return it.unifyInto(this, first, second) }
        val mgu = unificator.mgu(resolve(first), resolve(second), occurCheckEnabled = false)
        return mgu is Substitution.Unifier && copy(mgu, unificator)
    }

    /**
     * Adds the bindings of [unifier]: an unbound variable is bound, a bound one is unified with [unificator]. Returns
     * `false` if a unification fails, leaving the bindings made so far for the caller to [undo].
     */
    fun bindAll(
        unifier: Substitution.Unifier,
        unificator: Unificator,
    ): Boolean {
        val incremental = unificator.incremental ?: return copy(unifier, unificator)
        return unifier.all { (variable, value) -> incremental.unifyInto(this, variable, value) }
    }

    private fun copy(
        unifier: Substitution.Unifier,
        unificator: Unificator,
    ): Boolean {
        for ((variable, value) in unifier) {
            val current = deref(variable)
            if (current is Var) {
                if (current != deref(value)) bind(current, value)
            } else {
                val mgu = unificator.mgu(resolve(current), resolve(value), occurCheckEnabled = false)
                if (mgu !is Substitution.Unifier || !copy(mgu, unificator)) return false
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
        if (opaque) {
            return Substitution.of(
                variables.distinct().associateWith { resolveOpaque(it) }.filter { (variable, value) ->
                    variable != value
                },
            )
        }
        val resolved = HashMap<Var, Term>()
        for (variable in variables) if (variable !in resolved) resolveVar(variable, resolved)
        return Substitution.of(resolved.filter { (variable, value) -> variable != value })
    }

    /**
     * [term] resolved as it was when the trail was at [mark]: the bindings made since are ignored. A variable met again
     * while being resolved (a cyclic binding) is left as is.
     */
    fun resolveAt(
        term: Term,
        mark: Int,
    ): Term {
        if (mark >= trail.size || term.isGround) return resolve(term)
        val later = trail.subList(mark, trail.size).toHashSet()
        if (opaque) return applyToFixpoint(term, values.filterKeys { it !in later })
        val memo = HashMap<Var, Term>()

        fun substitute(
            target: Term,
            path: Set<Var>,
        ): Term {
            val replacements =
                target.variables
                    .distinct()
                    .mapNotNull { variable ->
                        val value =
                            memo[variable] ?: run {
                                val bound =
                                    (if (variable in later) null else values[variable]) ?: return@mapNotNull null
                                if (variable in path) return@mapNotNull null
                                substitute(bound, path + variable).also { memo[variable] = it }
                            }
                        (variable to value).takeIf { value != variable }
                    }.toMap()
            return if (replacements.isEmpty()) target else target.apply(Substitution.of(replacements))
        }
        return substitute(term, emptySet())
    }

    /** [term] with every bound variable replaced by its value, recursively. */
    fun resolve(term: Term): Term {
        if (values.isEmpty() || term.isGround) return term
        if (opaque) return resolveOpaque(term)
        val replacements = substitutionOf(term.variables)
        return if (replacements.isEmpty()) term else term.apply(replacements)
    }

    // shortcut: applies every binding until a fixpoint, at a cost linear in the bindings per call;
    // only solvers with opaque terms pay it, track their hidden variables if that ever matters
    private fun resolveOpaque(term: Term): Term = applyToFixpoint(term, values)

    private fun applyToFixpoint(
        term: Term,
        bindings: Map<Var, Term>,
    ): Term {
        val all = Substitution.of(bindings)
        var current = term
        repeat(bindings.size + 1) {
            val next = current.apply(all)
            if (next == current) return next
            current = next
        }
        return current
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

/** This unificator, or the one it caches, if it unifies in place; null to go through mgu. */
internal val Unificator.incremental: IncrementalUnificator?
    get() =
        when (this) {
            is IncrementalUnificator -> this
            // caching is irrelevant to unification in place
            is CachedUnificator -> decorated.incremental
            else -> null
        }

// allocation-free, as it runs on every binding; loops on the last argument so that long lists don't overflow the stack
private fun Term.hasOpaqueSubterm(): Boolean {
    var term = this
    while (true) {
        if (term.isGround || term is Var) return false
        if (term !is Struct || term.arity == 0) return true
        for (i in 0 until term.arity - 1) if (term[i].hasOpaqueSubterm()) return true
        term = term[term.arity - 1]
    }
}
