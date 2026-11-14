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
@get:JsName("functorVariables")
val Substitution.functorVariables: Set<Var>
    get() = (tags[FUNCTOR_VARIABLES_TAG] as? FunctorVariableSet)?.variables ?: emptySet()

/** This substitution, with [variables] as its [functorVariables] (in addition to the ones it has). */
internal fun Substitution.withFunctorVariables(variables: Set<Var>): Substitution =
    if (isFailed || variables.isEmpty() || functorVariables.containsAll(variables)) {
        this
    } else {
        replaceTags(tags + (FUNCTOR_VARIABLES_TAG to FunctorVariableSet(functorVariables + variables)))
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
    // allocated only for terms with more than one non-ground compound to visit: most terms have no pattern at all
    var pending: ArrayList<Term>? = null
    var current: Term? = term
    while (current != null) {
        if (current is Pattern) action(current.functor)
        var next: Term? = null
        val compound = current.asCompound()
        if (compound != null) {
            for (i in compound.arity - 1 downTo 0) {
                val arg = compound[i]
                if (arg is Var || arg.isGround) continue
                if (next != null) (pending ?: ArrayList<Term>().also { pending = it }).add(next)
                next = arg
            }
        }
        current = next ?: pending?.let { if (it.isEmpty()) null else it.removeAt(it.lastIndex) }
    }
}

/** The value of the [FUNCTOR_VARIABLES_TAG] tag: a type of its own, so that reading it needs no unchecked cast. */
private data class FunctorVariableSet(
    val variables: Set<Var>,
)
