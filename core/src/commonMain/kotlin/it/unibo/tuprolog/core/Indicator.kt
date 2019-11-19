package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.IndicatorImpl

/**
 * A predicate indicator is used to denote predicates or functors.
 *
 * It is a ground term of the form `Name/Arity` where `Name` is an [Atom] denoting the name of a predicate or a functor
 * and `Arity` is a non-negative [Integer] denoting the number of its arguments.
 *
 * Examples: `sumlist/2`, `'.'/2`, `'/'/2`, `animal/1`, `'1'/0`.
 *
 * @author Enrico
 */
interface Indicator : Struct {

    override val isIndicator: Boolean
        get() = true

    override val functor: String
        get() = FUNCTOR

    override val args: Array<Term>
        get() = arrayOf(nameTerm, arityTerm)

    override val arity: Int
        get() = 2

    /** The indicated functor name Term */
    val nameTerm: Term

    /** The indicated functor arity Term */
    val arityTerm: Term

    /**
     * Whether this Indicator is well-formed
     *
     * An indicator is well-formed when:
     * - its [nameTerm] is an [Atom]
     * - its [arityTerm] is a non-negative [Integer]
     */
    val isWellFormed: Boolean
        get() = nameTerm is Atom &&
                arityTerm is Integer &&
                arityTerm.`as`<Integer>().intValue.toInt() >= 0

    /** The indicated functor name, if well-formed */
    val indicatedName: String?
        get() = (nameTerm as? Atom)?.value

    /** The indicated functor arity, if well-formed */
    val indicatedArity: Int?
        get() = (arityTerm as? Integer)?.intValue?.toInt()?.takeIf { it >= 0 }

    override fun freshCopy(): Indicator = super.freshCopy() as Indicator

    override fun freshCopy(scope: Scope): Indicator =
        when {
            isGround -> this
            else -> scope.indicatorOf(nameTerm.freshCopy(scope), arityTerm.freshCopy(scope))
        }

    companion object {

        /** The canonical indicator functor: `/` */
        const val FUNCTOR = "/"

        /** Creates an indicator denoting functor named [name] with [arity] */
        fun of(name: Term, arity: Term): Indicator = IndicatorImpl(name, arity)

        /** Creates an indicator denoting functor named [name] with [arity] */
        fun of(name: String, arity: Int): Indicator = of(Atom.of(name), Integer.of(arity))
    }
}