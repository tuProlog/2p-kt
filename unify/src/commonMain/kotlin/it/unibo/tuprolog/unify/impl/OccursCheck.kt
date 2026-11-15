@file:JvmName("OccursCheck")

package it.unibo.tuprolog.unify.impl

import it.unibo.tuprolog.core.Compound
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import kotlin.jvm.JvmName

/**
 * Tells whether [variable] occurs in [term], once the variables in [term] are replaced by what they are bound to.
 * Iterative, and visiting each bound variable once, so it copes with deep terms, shared subterms and cyclic bindings.
 * Constants are skipped; groundness is not checked otherwise, as computing it is recursive for some terms.
 */
internal fun Bindings.occurs(
    variable: Var,
    term: Term,
): Boolean {
    val pending = ArrayList<Term>()
    pending.add(term)
    var visited: MutableSet<Var>? = null
    while (pending.isNotEmpty()) {
        val current = pending.removeAt(pending.lastIndex)
        if (current is Var) {
            if (current == variable) return true
            val value = this[current] ?: continue
            val seen = visited ?: HashSet<Var>().also { visited = it }
            if (seen.add(current)) pending.add(value)
        } else if (current is Compound && pending.pushArgumentsOf(current, variable)) {
            return true
        }
    }
    return false
}

/**
 * Pushes the arguments of [compound] worth visiting (and the functor of a pattern), unless [variable] is one of them:
 * then, returns `true`. Out of [occurs], which stays small enough to be inlined.
 */
private fun ArrayList<Term>.pushArgumentsOf(
    compound: Compound,
    variable: Var,
): Boolean {
    for (i in 0 until compound.arity) {
        val arg = compound[i]
        if (arg is Var && arg == variable) return true
    }
    // last argument first, so that list spines are visited with a constant amount of pending terms
    for (i in compound.arity - 1 downTo 0) {
        val arg = compound[i]
        if (arg is Var || (arg is Compound && arg.arity > 0)) add(arg)
    }
    // a property rather than a type check: failed checks against interfaces are slow on the JVM
    if (compound.isPattern) add(compound.castToPattern().functor)
    return false
}

/** Tells whether any variable of this substitution occurs in what it is bound to. */
internal fun Substitution.hasCycles(): Boolean {
    val bindings = Bindings(this)
    return entries.any { (variable, term) -> bindings.occurs(variable, term) }
}
