package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.toTerm
import kotlin.js.JsName

/**
 * A class representing an Equation of logic terms, to be unified;
 *
 * LHS stands for Left-Hand side and RHS stands for Right-Hand side, of the Equation
 */
data class Equation<out A : Term, out B : Term>(
        /** The left-hand side of the equation */
        val lhs: A,
        /** The right-hand side of the equation */
        val rhs: B) {

    /** Swaps the [Equation]; eg. A=B becomes B=A */
    fun swap(): Equation<B, A> = Equation(rhs, lhs)

    /** Transforms this [Equation] to an equivalent [Pair] */
    fun toPair(): Pair<A, B> = Pair(lhs, rhs)

    /** Equation companion object */
    companion object {

        /** Creates an Equation with provided left-hand and right-hand sides */
        fun <A : Term, B : Term> of(lhs: A, rhs: B): Equation<A, B> = Equation(lhs, rhs)

        /**
         * Creates an Equation with provided objects, prior transforming them to [Term]s;
         *
         * An exception could be thrown if given objects cannot be converted to [Term]s
         */
        fun of(lhs: Any, rhs: Any): Equation<Term, Term> = of(lhs.toTerm(), rhs.toTerm())

        /** Creates an [Equation] from the given [Pair] */
        fun <A : Term, B : Term> from(pair: Pair<A, B>): Equation<A, B> = Equation(pair.first, pair.second)
    }
}

/** Symbolic equation creation */
@JsName("termEq")
infix fun <A : Term, B : Term> A.`=`(that: B): Equation<A, B> = Equation(this, that)

/**
 * Symbolic equation creation; could throw an exception if given objects are not convertible to [Term]s
 */
@JsName("anyEq")
infix fun Any.`=`(that: Any): Equation<Term, Term> = Equation.of(this, that)

/** Transforms an [Equation] of a [Var] with a [Term] to the corresponding [Substitution] */
fun <A : Var, B : Term> Equation<A, B>.toSubstitution(): Substitution = Substitution.of(this.toPair())

/** Transforms a [Substitution] into the list of corresponding [Equation]s */
fun Substitution.toEquations(): List<Equation<Var, Term>> = this.entries.map { (variable, term) -> variable `=` term }
