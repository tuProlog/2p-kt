@file:JvmName("FunctorVariables")

package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Pattern
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import kotlin.js.JsName
import kotlin.jvm.JvmName

/** The tag under which a [Substitution] returned by a [Unificator] lists its [functorVariables]. */
const val FUNCTOR_VARIABLES_TAG = "it.unibo.tuprolog.unify.functor-variables"

/**
 * The *functor variables* of this substitution: variables standing for the functor of some [Pattern], which can only be
 * bound to atoms or variables. Unificators record them in the substitutions they return (and read them from the ones
 * they are given), so that the restriction holds across unifications, e.g. after `P(X) = Q(a)`, `Q = f(b)` fails.
 */
@Suppress("UNCHECKED_CAST")
@get:JsName("functorVariables")
val Substitution.functorVariables: Set<Var>
    get() = tags[FUNCTOR_VARIABLES_TAG] as? Set<Var> ?: emptySet()

/** This substitution, with [variables] as its [functorVariables] (in addition to the ones it has). */
internal fun Substitution.withFunctorVariables(variables: Set<Var>): Substitution =
    if (isFailed || variables.isEmpty() || functorVariables.containsAll(variables)) {
        this
    } else {
        replaceTags(tags + (FUNCTOR_VARIABLES_TAG to (functorVariables + variables)))
    }

/**
 * Calls [action] on the functor of each [Pattern] in [term] (variables are not followed). Ground subterms are skipped,
 * as patterns are never ground, and so is everything while no pattern exists at all (see [Pattern.anyCreated]).
 */
internal inline fun forEachPatternFunctor(
    term: Term,
    action: (Var) -> Unit,
) {
    if (!Pattern.anyCreated || term is Var || term.isGround) return
    val pending = ArrayList<Term>()
    pending.add(term)
    while (pending.isNotEmpty()) {
        val current = pending.removeAt(pending.lastIndex)
        if (current is Pattern) action(current.functor)
        val compound = current.asCompound() ?: continue
        for (i in compound.arity - 1 downTo 0) {
            val arg = compound[i]
            if (arg !is Var && !arg.isGround) pending.add(arg)
        }
    }
}
