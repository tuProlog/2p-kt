package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.*
import kotlin.js.JsName
import kotlin.jvm.JvmStatic
import kotlin.collections.List as KtList

/**
 * A class representing an Equation of logic terms, to be unified;
 *
 * LHS stands for Left-Hand side and RHS stands for Right-Hand side, of the Equation
 */
sealed class Equation<out A : Term, out B : Term>(
    /** The left-hand side of the equation */
    open val lhs: A,
    /** The right-hand side of the equation */
    open val rhs: B
) : ToTermConvertible {


    /** An equation of identical [Term]s */
    data class Identity<out T : Term>(override val lhs: T, override val rhs: T) : Equation<T, T>(lhs, rhs)

    /** An equation stating [Var] = [Term] */
    data class Assignment<out A : Var, out B : Term>(override val lhs: A, override val rhs: B) :
        Equation<A, B>(lhs, rhs)

    /** An equation comparing [Term]s, possibly different */
    data class Comparison<out A : Term, out B : Term>(override val lhs: A, override val rhs: B) :
        Equation<A, B>(lhs, rhs)

    /** A contradicting equation, trying to equate non equal [Term]s */
    data class Contradiction<out A : Term, out B : Term>(override val lhs: A, override val rhs: B) :
        Equation<A, B>(lhs, rhs)

    override fun toTerm(): Struct = Struct.of("=", lhs, rhs)

    fun toPair(): Pair<A, B> = Pair(lhs, rhs)

    fun swap(): Equation<Term, Term> = of(rhs, lhs)

    /**
     * Applies given [substitution] to the Equation left-hand and right-hand sides, returning the new Equation
     *
     * To modify default equality between [Term]s, a custom [equalityChecker] can be provided
     */
    fun apply(
        substitution: Substitution,
        equalityChecker: (Term, Term) -> Boolean = Term::equals
    ): Equation<Term, Term> =
        of(lhs[substitution], rhs[substitution], equalityChecker)


    /** Equation companion object */
    companion object {

        /** Creates an Equation with provided left-hand and right-hand sides */
        @JvmStatic
        fun <A : Term, B : Term> of(
            lhs: A, rhs: B,
            equalityChecker: (Term, Term) -> Boolean = Term::equals
        ): Equation<Term, Term> =
            when {
                lhs is Var && rhs is Var -> if (equalityChecker(lhs, rhs)) Identity(lhs, rhs) else Assignment(lhs, rhs)
                lhs is Var -> Assignment(lhs, rhs)
                rhs is Var -> Assignment(rhs, lhs)
                lhs is Constant && rhs is Constant ->
                    if (equalityChecker(lhs, rhs)) Identity(lhs, rhs)
                    else Contradiction(lhs, rhs)
                lhs is Constant || rhs is Constant -> Contradiction(lhs, rhs)
                lhs is Struct && rhs is Struct && (lhs.arity != rhs.arity || lhs.functor != rhs.functor) ->
                    Contradiction(lhs, rhs)
                else -> Comparison(lhs, rhs)
            }

        /** Creates an Equation from given [Pair] */
        @JvmStatic
        fun <A : Term, B : Term> of(
            pair: Pair<A, B>,
            equalityChecker: (Term, Term) -> Boolean = Term::equals
        ): Equation<Term, Term> =
            of(pair.first, pair.second, equalityChecker)

        /** Creates all equations resulting from the deep inspection of given [Pair] of [Term]s */
        @JvmStatic
        fun <A : Term, B : Term> allOf(
            pair: Pair<A, B>,
            equalityChecker: (Term, Term) -> Boolean = Term::equals
        ): Sequence<Equation<Term, Term>> =
            allOf(pair.first, pair.second, equalityChecker)

        /** Creates all equations resulting from the deep inspection of provided left-hand and right-hand sides' [Term] */
        @JvmStatic
        fun <A : Term, B : Term> allOf(
            lhs: A, rhs: B,
            equalityChecker: (Term, Term) -> Boolean = Term::equals
        ): Sequence<Equation<Term, Term>> =
            when {
                (lhs !is Constant && rhs !is Constant && lhs !is Var && rhs !is Var) &&
                        lhs is Struct && rhs is Struct &&
                        lhs.arity == rhs.arity &&
                        lhs.functor == rhs.functor ->

                    lhs.argsSequence.zip(rhs.argsSequence).flatMap { allOf(it, equalityChecker) }

                else -> sequenceOf(of(lhs, rhs, equalityChecker))
            }
    }
}


