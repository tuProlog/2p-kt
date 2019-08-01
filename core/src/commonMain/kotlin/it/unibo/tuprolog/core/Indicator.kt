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
        get() = arrayOf(indicatedName, indicatedArity)

    override val arity: Int
        get() = 2

    /** The indicated functor name */
    val indicatedName: Term

    /** The indicated functor arity */
    val indicatedArity: Term

    /**
     * Whether this Indicator is well-formed
     *
     * An indicator is well-formed when:
     * - its [indicatedName] is an [Atom]
     * - its [indicatedArity] is a non-negative [Integer]
     */
    val isWellFormed: Boolean
        get() = indicatedName is Atom &&
                indicatedArity is Integer &&
                indicatedArity.`as`<Integer>().intValue.toInt() >= 0

    override fun freshCopy(): Indicator = super.freshCopy() as Indicator

    override fun freshCopy(scope: Scope): Indicator =
            when {
                isGround -> this
                else -> scope.indicatorOf(indicatedName.freshCopy(scope), indicatedArity.freshCopy(scope))
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