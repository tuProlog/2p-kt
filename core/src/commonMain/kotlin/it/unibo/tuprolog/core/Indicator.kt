package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.Terms.INDICATOR_FUNCTOR
import it.unibo.tuprolog.core.impl.IndicatorImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A predicate indicator is used to denote predicates or functors.
 *
 * It is a term of the form `Name/Arity` where `Name` is an [Atom] denoting the name of a predicate or a functor
 * and `Arity` is a non-negative [Integer] denoting the number of its arguments.
 * `Name` may also be a [Var], denoting the predicate symbol of a [Pattern] (e.g. `P/2`): such an indicator is
 * well-formed, but not [concrete][isConcrete], hence it does not identify any specific procedure.
 *
 * Examples: `sumlist/2`, `'.'/2`, `'/'/2`, `animal/1`, `'1'/0`, `P/2`.
 *
 * @author Enrico
 */
interface Indicator : Struct {
    override val isIndicator: Boolean
        get() = true

    override val functor: String
        get() = INDICATOR_FUNCTOR

    override val arity: Int
        get() = 2

    /** The indicated functor name Term */
    @JsName("nameTerm")
    val nameTerm: Term

    /** The indicated functor arity Term */
    @JsName("arityTerm")
    val arityTerm: Term

    /**
     * Whether this Indicator is well-formed
     *
     * An indicator is well-formed when:
     * - its [nameTerm] is either an [Atom] or a [Var]
     * - its [arityTerm] is a non-negative [Integer]
     */
    @JsName("isWellFormed")
    val isWellFormed: Boolean
        get() =
            (nameTerm.isAtom || nameTerm.isVar) &&
                arityTerm.let { it.isInteger && it.castToInteger().intValue.signum >= 0 }

    /**
     * Whether this Indicator is well-formed and its [nameTerm] is an [Atom], hence it denotes a specific procedure.
     * Check this, rather than [isWellFormed], before deriving a concrete procedure signature.
     */
    @JsName("isConcrete")
    val isConcrete: Boolean
        get() = nameTerm.isAtom && isWellFormed

    /** The indicated functor name, if well-formed */
    @JsName("indicatedName")
    val indicatedName: String?
        get() = nameTerm.asAtom()?.value

    /** The indicated functor arity, if well-formed */
    @JsName("indicatedArity")
    val indicatedArity: Int?
        get() =
            arityTerm
                .asInteger()
                ?.intValue
                ?.toInt()
                ?.takeIf { it >= 0 }

    override fun asIndicator(): Indicator = this

    override fun freshCopy(): Indicator

    override fun freshCopy(scope: Scope): Indicator

    operator fun component1(): Term = nameTerm

    operator fun component2(): Term = arityTerm

    companion object {
        /** The canonical indicator functor: `/` */
        const val FUNCTOR = INDICATOR_FUNCTOR

        /** Creates an indicator denoting functor named [name] with [arity] */
        @JvmStatic
        @JsName("of")
        fun of(
            name: Term,
            arity: Term,
        ): Indicator = IndicatorImpl(name, arity)

        /** Creates a (non-concrete) indicator whose name is the variable [name], with [arity] */
        @JvmStatic
        @JsName("ofVar")
        fun of(
            name: Var,
            arity: Int,
        ): Indicator = of(name, Integer.of(arity))

        /** Creates an indicator denoting functor named [name] with [arity] */
        @JvmStatic
        @JsName("ofString")
        fun of(
            name: String,
            arity: Int,
        ): Indicator = of(Atom.of(name), Integer.of(arity))
    }
}
