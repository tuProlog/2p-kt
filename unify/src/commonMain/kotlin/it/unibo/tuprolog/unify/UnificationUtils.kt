@file:JvmName("UnificationUtils")

package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import kotlin.jvm.JvmName

/** Creates a [Substitution] out of a [Iterable] of [Equation]s assigning [Var]s to [Term]s  */
fun Iterable<Equation>.toSubstitution(): Substitution = Substitution.of(this.asSequence().map { it.toAssignmentPair() })

/** Transforms a [Substitution] into the list of corresponding [Equation]s */
fun Substitution.toEquations(): List<Equation> =
    this.entries.map { (variable, term) ->
        Equation.LeftAssignment(variable, term)
    }

/** Creates an equation with [this] and [that] terms */
@Suppress("unused", "FunctionName")
infix fun Term.eq(that: Term): Equation = Equation.of(this, that)
