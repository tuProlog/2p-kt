package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.*
import kotlin.collections.List
import kotlin.js.JsName
import kotlin.reflect.KClass


/**
 * A class representing an Equation of logic terms, to be unified;
 *
 * LHS stands for Left-Hand side and RHS stands for Right-Hand side, of the Equation
 */
sealed class Equation<out A : Term, out B : Term>(
        /** The left-hand side of the equation */
        open val lhs: A?,
        /** The right-hand side of the equation */
        open val rhs: B?) {

    data class Identity<out A : Term, out B: Term>(override val lhs: A, override val rhs: B) : Equation<A, B>(lhs, rhs)

    data class Assignment<out A : Var, out B: Term>(override val lhs: A, override val rhs: B) : Equation<A, B>(lhs, rhs)

    data class Comparison<out A : Term, out B: Term>(override val lhs: A, override val rhs: B) : Equation<A, B>(lhs, rhs)

    object Contradiction: Equation<Nothing, Nothing>(null, null)

    fun toPair(): Pair<A, B> = Pair(lhs!!, rhs!!)

    fun swap(): Equation<Term, Term> = of(rhs, lhs)

    fun apply(substitution: Substitution, equalityChecker: (Term, Term)->Boolean = Term::equals): Equation<Term, Term> {
        return Equation.of(lhs!![substitution], rhs!![substitution], equalityChecker)
    }


    /** Equation companion object */
    companion object {

        fun <A : Term, B : Term> of(pair: Pair<A?, B?>, equalityChecker: (Term, Term)->Boolean = Term::equals): Equation<Term, Term> {
            return of(pair.first, pair.second, equalityChecker)
        }

            /** Creates an Equation with provided left-hand and right-hand sides */
        fun <A : Term, B : Term> of(lhs: A?, rhs: B?, equalityChecker: (Term, Term)->Boolean = Term::equals): Equation<Term, Term> {
            return when {
                lhs === null || rhs === null -> Contradiction
                lhs is Var && rhs is Var -> if (equalityChecker(lhs, rhs)) Identity(lhs, rhs) else Assignment(lhs, rhs)
                lhs is Var -> Assignment(lhs, rhs)
                rhs is Var -> Assignment(rhs, lhs)
                lhs is Constant && rhs is Constant -> if (equalityChecker(lhs, rhs)) Identity(lhs, rhs) else Contradiction
                (lhs is Constant &&  rhs !is Constant) || (lhs !is Constant &&  rhs is Constant) -> Contradiction
                lhs is Struct && rhs is Struct && (lhs.arity != rhs.arity || lhs.functor != rhs.functor) -> Contradiction
                else -> Comparison(lhs, rhs)
            }
        }

        fun <A : Term, B : Term> allOf(pair: Pair<A?, B?>, equalityChecker: (Term, Term)->Boolean = Term::equals): Sequence<Equation<Term, Term>> {
            return allOf(pair.first, pair.second, equalityChecker)
        }

        fun <A : Term, B : Term> allOf(lhs: A?, rhs: B?, equalityChecker: (Term, Term)->Boolean = Term::equals): Sequence<Equation<Term, Term>>  {
            return when {
                lhs === null || rhs === null ->
                    sequenceOf(Contradiction)
                lhs is Var && rhs is Var ->
                    if (equalityChecker(lhs, rhs))
                        sequenceOf(Identity(lhs, rhs))
                    else
                        sequenceOf(Assignment(lhs, rhs))
                lhs is Var ->
                    sequenceOf(Assignment(lhs, rhs))
                rhs is Var ->
                    sequenceOf(Assignment(rhs, lhs))
                lhs is Constant && rhs is Constant ->
                    if (equalityChecker(lhs, rhs))
                        sequenceOf(Identity(lhs, rhs))
                    else
                        sequenceOf(Contradiction)
                (lhs is Constant &&  rhs !is Constant) || (lhs !is Constant &&  rhs is Constant) ->
                    sequenceOf(Contradiction)
                lhs is Struct && rhs is Struct ->
                    if (lhs.arity == rhs.arity && lhs.functor == rhs.functor)
                        lhs.argsSequence.zip(rhs.argsSequence).flatMap { allOf(it, equalityChecker) }
                    else
                        sequenceOf(Contradiction)
                else ->
                    sequenceOf(Comparison(lhs, rhs))
            }
        }
    }
}

/** Transforms an [Equation] of a [Var] with a [Term] to the corresponding [Substitution] */
fun <A : Var, B : Term> Equation<A, B>.toSubstitution(): Substitution = Substitution.of(this.toPair())

/** Transforms a [Substitution] into the list of corresponding [Equation]s */
fun Substitution.toEquations(): List<Equation<Var, Term>> =
        this.entries.map { (variable, term) ->  Equation.Assignment(variable, term) }
