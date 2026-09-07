package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.FactImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A [Rule] whose [body] is (equivalent to) the `true` atom, e.g. `parent(tom, bob).`. [Rule.of] and
 * [Clause.of] already return a [Fact] automatically whenever the body they are given reduces to `true`;
 * [Fact.of] is a shortcut for when the absence of a body is known upfront.
 */
interface Fact : Rule {
    override val body: Term
        get() = Truth.TRUE

    override val isFact: Boolean
        get() = true

    override fun freshCopy(): Fact

    override fun freshCopy(scope: Scope): Fact

    override fun asFact(): Fact = this

    override fun setHeadFunctor(functor: String): Fact

    override fun setHeadArgs(vararg arguments: Term): Fact

    override fun setHeadArgs(arguments: Iterable<Term>): Fact

    override fun setHeadArgs(arguments: Sequence<Term>): Fact

    override fun insertHeadArg(
        index: Int,
        argument: Term,
    ): Fact

    override fun addFirstHeadArg(argument: Term): Fact

    override fun addLastHeadArg(argument: Term): Fact

    override fun appendHeadArg(argument: Term): Fact

    companion object {
        /** The canonical clause functor: `:-` (same as [Clause.FUNCTOR]). */
        const val FUNCTOR = Terms.CLAUSE_FUNCTOR

        /** Creates a [Fact] with the given [head]. */
        @JvmStatic
        @JsName("of")
        fun of(head: Struct): Fact = FactImpl(head)

        /**
         * Creates a [Fact] template: a head with [functor] and [arity] anonymous-variable arguments. See
         * [Struct.template].
         * @throws IllegalArgumentException if [arity] is negative
         */
        @JvmStatic
        @JsName("template")
        fun template(
            functor: String,
            arity: Int,
        ): Fact = of(Struct.template(functor, arity))
    }
}
