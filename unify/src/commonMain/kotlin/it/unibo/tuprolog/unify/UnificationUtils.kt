@file:JvmName("UnificationUtils")

package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import kotlin.jvm.JvmName

/**
 * Creates a [Substitution] out of an [Iterable] of [Equation]s, each assigning a [Var] to a [Term] (see
 * [Equation.toAssignmentPair]). Returns [Substitution.failed] if two equations disagree on the same [Var]'s binding.
 *
 * @throws IllegalArgumentException if any [Equation] in this [Iterable] is not an [Equation.Assignment] (has no
 * [Var] on either side)
 */
fun Iterable<Equation>.toSubstitution(): Substitution = Substitution.of(this.asSequence().map { it.toAssignmentPair() })

/** Transforms this [Substitution]'s bindings into the equivalent list of [Equation.LeftAssignment]s. */
fun Substitution.toEquations(): List<Equation> =
    this.entries.map { (variable, term) ->
        Equation.LeftAssignment(variable, term)
    }

/** Creates an [Equation] with [this] as left-hand side and [that] as right-hand side (see [Equation.of]). */
@Suppress("unused", "FunctionName")
infix fun Term.eq(that: Term): Equation = Equation.of(this, that)
