package it.unibo.tuprolog.unify

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.ToTermConvertible
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.Var
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic
import it.unibo.tuprolog.core.List as LogicList

/**
 * A class representing an Equation of logic terms, to be unified;
 *
 * LHS stands for Left-Hand side and RHS stands for Right-Hand side, of the Equation
 */
sealed class Equation(
    /** The left-hand side of the equation */
    @JsName("lhs") open val lhs: Term,
    /** The right-hand side of the equation */
    @JsName("rhs") open val rhs: Term
) : ToTermConvertible {

    /** An equation of identical [Term]s */
    data class Identity(override val lhs: Term, override val rhs: Term) : Equation(lhs, rhs)

    /** An equation stating [Var] = [Term] */
    data class Assignment(override val lhs: Var, override val rhs: Term) : Equation(lhs, rhs) {
        @JsName("toSubstitution")
        fun toSubstitution(): Substitution.Unifier = Substitution.unifier(lhs, rhs)
    }

    /** An equation comparing [Term]s, possibly different */
    data class Comparison(override val lhs: Term, override val rhs: Term) : Equation(lhs, rhs)

    /** A contradicting equation, trying to equate non equal [Term]s */
    data class Contradiction(override val lhs: Term, override val rhs: Term) : Equation(lhs, rhs)

    override fun toTerm(): Struct = Struct.of("=", lhs, rhs)

    @JsName("toPair")
    fun toPair(): Pair<Term, Term> = Pair(lhs, rhs)

    @JsName("swap")
    fun swap(): Equation = of(rhs, lhs)

    /**
     * Applies given [substitution] to the Equation left-hand and right-hand sides, returning the new Equation
     *
     * To modify default equality between [Term]s, a custom [equalityChecker] can be provided
     */
    @JvmOverloads
    @JsName("apply")
    fun apply(
        substitution: Substitution,
        equalityChecker: (Term, Term) -> Boolean = Term::equals
    ): Equation = of(lhs[substitution], rhs[substitution], equalityChecker)

    /** Equation companion object */
    companion object {

        /** Creates an [Equation] with provided left-hand and right-hand sides */
        @JvmStatic
        @JvmOverloads
        @JsName("of")
        fun of(lhs: Term, rhs: Term, equalityChecker: (Term, Term) -> Boolean = Term::equals): Equation =
            when {
                lhs.isVariable && rhs.isVariable -> {
                    if (equalityChecker(lhs, rhs)) {
                        Identity(lhs, rhs)
                    } else {
                        Assignment(lhs.castToVar(), rhs)
                    }
                }
                lhs.isVariable -> Assignment(lhs.castToVar(), rhs)
                rhs.isVariable -> Assignment(rhs.castToVar(), lhs)
                lhs.isConstant && rhs.isConstant -> {
                    if (equalityChecker(lhs, rhs)) {
                        Identity(lhs, rhs)
                    } else {
                        Contradiction(lhs, rhs)
                    }
                }
                lhs.isConstant || rhs.isConstant -> Contradiction(lhs, rhs)
                lhs.isStruct && rhs.isStruct -> {
                    val lhsStruct = lhs.castToStruct()
                    val rhsStruct = rhs.castToStruct()
                    if (lhsStruct.arity != rhsStruct.arity || lhsStruct.functor != rhsStruct.functor) {
                        Contradiction(lhsStruct, rhsStruct)
                    } else {
                        Comparison(lhsStruct, rhsStruct)
                    }
                }
                else -> Comparison(lhs, rhs)
            }

        /** Creates an [Equation] from given [Pair] */
        @JvmStatic
        @JvmOverloads
        @JsName("ofPair")
        fun of(
            pair: Pair<Term, Term>,
            equalityChecker: (Term, Term) -> Boolean = Term::equals
        ): Equation = of(pair.first, pair.second, equalityChecker)

        @JvmStatic
        @JvmOverloads
        @JsName("fromSequence")
        fun from(
            pairs: Sequence<Pair<Term, Term>>,
            equalityChecker: (Term, Term) -> Boolean = Term::equals
        ): Sequence<Equation> = pairs.flatMap { allOf(it, equalityChecker) }

        @JvmStatic
        @JvmOverloads
        @JsName("fromIterable")
        fun from(
            pairs: Iterable<Pair<Term, Term>>,
            equalityChecker: (Term, Term) -> Boolean = Term::equals
        ): Sequence<Equation> = from(pairs.asSequence(), equalityChecker)

        @JvmStatic
        @JvmOverloads
        @JsName("from")
        fun from(
            vararg pairs: Pair<Term, Term>,
            equalityChecker: (Term, Term) -> Boolean = Term::equals
        ): Sequence<Equation> = from(sequenceOf(*pairs), equalityChecker)

        /** Creates all equations resulting from the deep inspection of given [Pair] of [Term]s */
        @JvmStatic
        @JvmOverloads
        @JsName("allOfPair")
        fun allOf(
            pair: Pair<Term, Term>,
            equalityChecker: (Term, Term) -> Boolean = Term::equals
        ): Sequence<Equation> = allOf(pair.first, pair.second, equalityChecker)

        private fun allOfLists(lhs: LogicList, rhs: LogicList, equalityChecker: (Term, Term) -> Boolean = Term::equals): Sequence<Equation> =
            lhs.unfold().zip(rhs.unfold()).flatMap { (l, r) ->
                when {
                    l.isCons && r.isCons -> sequenceOf(of(l.castToCons().head, r.castToCons().head, equalityChecker))
                    l.isList && r.isList -> sequenceOf(of(l.castToList(), r.castToList(), equalityChecker))
                    else -> allOf(l, r, equalityChecker)
                }
            }

        private fun allOfTuples(lhs: Tuple, rhs: Tuple, equalityChecker: (Term, Term) -> Boolean = Term::equals): Sequence<Equation> =
            lhs.unfold().zip(rhs.unfold()).flatMap { (l, r) ->
                when {
                    l.isTuple && r.isTuple -> sequenceOf(of(l.castToTuple().left, r.castToTuple().left, equalityChecker))
                    else -> allOf(l, r, equalityChecker)
                }
            }

        /** Creates all equations resulting from the deep inspection of provided left-hand and right-hand sides' [Term] */
        @JvmStatic
        @JvmOverloads
        @JsName("allOf")
        fun allOf(lhs: Term, rhs: Term, equalityChecker: (Term, Term) -> Boolean = Term::equals): Sequence<Equation> =
            when {
                lhs.isAtom && rhs.isAtom -> {
                    sequenceOf(of(lhs, rhs, equalityChecker))
                }
                lhs.isList && rhs.isList -> {
                    allOfLists(lhs.castToList(), rhs.castToList(), equalityChecker)
                }
                lhs.isTuple && rhs.isTuple -> {
                    allOfTuples(lhs.castToTuple(), rhs.castToTuple(), equalityChecker)
                }
                lhs.isStruct && rhs.isStruct -> {
                    val lhsStruct = lhs.castToStruct()
                    val rhsStruct = rhs.castToStruct()
                    if (lhsStruct.arity == rhsStruct.arity && lhsStruct.functor == rhsStruct.functor) {
                        lhsStruct.argsSequence.zip(rhsStruct.argsSequence).flatMap { allOf(it, equalityChecker) }
                    } else {
                        sequenceOf(of(lhs, rhs, equalityChecker))
                    }
                }
                else -> {
                    sequenceOf(of(lhs, rhs, equalityChecker))
                }
            }
    }
}
