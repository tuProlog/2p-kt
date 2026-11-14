package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.PatternImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic
import kotlin.collections.List as KtList

/**
 * A [Compound] whose functor is a [Var], e.g. `P(X, Y)`: the building block of metarules such as
 * `P(A, B) :- Q(A, C), R(C, B)`. Its arity is always at least 1, and the functor is an ordinary [Var].
 *
 * Applying a substitution binding the functor to an [Atom] yields a [Struct] (e.g. `P(a)` with `{P/p}` gives `p(a)`),
 * binding it to another [Var] yields a [Pattern], whereas binding it to any other term is an error.
 */
interface Pattern : Compound {
    override val isPattern: Boolean
        get() = true

    override fun asPattern(): Pattern = this

    override val functor: Var

    override val functorTerm: Var
        get() = functor

    override val indicator: Indicator
        get() = Indicator.of(functor, Integer.of(arity))

    override val variables: Sequence<Var>
        get() = sequenceOf(functor) + argsSequence.flatMap { it.variables }

    override val isGround: Boolean
        get() = false

    override fun freshCopy(): Pattern

    override fun freshCopy(scope: Scope): Pattern

    /** Creates a copy of this [Pattern] whose arguments are [args]. */
    override fun setArgs(vararg args: Term): Pattern

    /** @see setArgs */
    override fun setArgs(args: Iterable<Term>): Pattern

    /** @see setArgs */
    override fun setArgs(args: Sequence<Term>): Pattern

    /** Creates a copy of this [Pattern] whose functor is [functor]. */
    @JsName("setFunctor")
    fun setFunctor(functor: Var): Pattern

    companion object {
        /**
         * Creates a new [Pattern] given its [functor] and (non-empty) [args].
         * @throws IllegalArgumentException if [args] is empty
         */
        @JvmStatic
        @JsName("ofList")
        fun of(
            functor: Var,
            args: KtList<Term>,
        ): Pattern {
            require(args.isNotEmpty()) { "A pattern must have at least one argument: $functor()" }
            return PatternImpl(functor, args)
        }

        /** @see of */
        @JvmStatic
        @JsName("of")
        fun of(
            functor: Var,
            vararg args: Term,
        ): Pattern = of(functor, args.toList())

        /** @see of */
        @JvmStatic
        @JsName("ofIterable")
        fun of(
            functor: Var,
            args: Iterable<Term>,
        ): Pattern = of(functor, args.toList())

        /** @see of */
        @JvmStatic
        @JsName("ofSequence")
        fun of(
            functor: Var,
            args: Sequence<Term>,
        ): Pattern = of(functor, args.toList())
    }
}
