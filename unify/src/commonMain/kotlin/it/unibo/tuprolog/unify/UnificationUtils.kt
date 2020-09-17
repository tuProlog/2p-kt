@file:JvmName("UnificationUtils")

package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import kotlin.jvm.JvmName

/** Transforms an [Equation] of a [Var] with a [Term] to the corresponding [Substitution] */
fun <A : Var, B : Term> Equation<A, B>.toSubstitution(): Substitution =
    Substitution.of(this.toPair())

/** Creates a [Substitution] out of a [Iterable] of [Equation]s assigning [Var]s to [Term]s  */
fun <A : Var, B : Term> Iterable<Equation<A, B>>.toSubstitution(): Substitution =
    Substitution.of(this.map { it.toPair() })

/** Transforms a [Substitution] into the list of corresponding [Equation]s */
fun Substitution.toEquations(): List<Equation<Var, Term>> =
    this.entries.map { (variable, term) -> Equation.Assignment(variable, term) }

/** Creates an equation with [this] and [that] terms */
@Suppress("unused", "FunctionName")
infix fun Term.eq(that: Term): Equation<Term, Term> = Equation.of(this, that)
