package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.RuleImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A [Clause] with a non-`null` [head], i.e. `head :- body`. [Fact] is the special case where [body] is
 * (equivalent to) the `true` atom; [Rule.of] automatically returns a [Fact] whenever [body] reduces to `true`,
 * so most code building rules can just call [Rule.of] uniformly and let it decide.
 */
interface Rule : Clause {
    override val head: Struct

    override val isRule: Boolean
        get() = true

    override val isFact: Boolean
        get() = body.isTrue

    override val isDirective: Boolean
        get() = false

    override fun freshCopy(): Rule

    override fun freshCopy(scope: Scope): Rule

    override fun asRule(): Rule = this

    /** The arguments of [head]. Alias for `head.args`. */
    @JsName("headArgs")
    val headArgs: Iterable<Term>

    /** The arity of [head]. Alias for `head.arity`. */
    @JsName("headArity")
    val headArity: Int

    /**
     * Gets the [index]-th argument of [head].
     * @throws IndexOutOfBoundsException if [index] is out of [head]'s argument bounds
     */
    @JsName("getHeadArg")
    fun getHeadArg(index: Int): Term

    override fun setBody(body: Term): Rule

    override fun setHeadFunctor(functor: String): Rule

    override fun setHeadArgs(vararg arguments: Term): Rule

    override fun setHeadArgs(arguments: Iterable<Term>): Rule

    override fun setHeadArgs(arguments: Sequence<Term>): Rule

    override fun insertHeadArg(
        index: Int,
        argument: Term,
    ): Rule

    override fun addFirstHeadArg(argument: Term): Rule

    override fun addLastHeadArg(argument: Term): Rule

    override fun appendHeadArg(argument: Term): Rule

    override fun setBodyItems(
        argument: Term,
        vararg arguments: Term,
    ): Rule

    override fun setBodyItems(arguments: Iterable<Term>): Rule

    override fun setBodyItems(arguments: Sequence<Term>): Rule

    override fun insertBodyItem(
        index: Int,
        argument: Term,
    ): Rule

    override fun addFirstBodyItem(argument: Term): Rule

    override fun addLastBodyItem(argument: Term): Rule

    override fun appendBodyItem(argument: Term): Rule

    companion object {
        /** The canonical clause functor: `:-` (same as [Clause.FUNCTOR]). */
        const val FUNCTOR = Terms.CLAUSE_FUNCTOR

        /**
         * Creates a [Rule] with the given [head] and [body] goals (folded into a single [Term] via [Tuple]
         * when there is more than one). If [body] is empty, or reduces to a single `true` goal, a [Fact] is
         * returned instead of a generic [Rule].
         */
        @JvmStatic
        @JsName("of")
        fun of(
            head: Struct,
            vararg body: Term,
        ): Rule = of(head, body.asIterable())

        /** @see of */
        @JvmStatic
        @JsName("ofIterable")
        fun of(
            head: Struct,
            body: Iterable<Term>,
        ): Rule {
            val i = body.iterator()
            if (!i.hasNext()) return Fact.of(head)
            val first = i.next()
            if (!i.hasNext() && first.isTrue) return Fact.of(head)
            return RuleImpl(head, Tuple.wrapIfNeeded(body))
        }

        /** @see of */
        @JvmStatic
        @JsName("ofSequence")
        fun of(
            head: Struct,
            body: Sequence<Term>,
        ): Rule = of(head, body.asIterable())

        /**
         * Creates a [Rule] template: a head with [functor] and [arity] anonymous-variable arguments, and an
         * anonymous-variable body (matching anything). See [Struct.template].
         * @throws IllegalArgumentException if [arity] is negative
         */
        @JvmStatic
        @JsName("template")
        fun template(
            functor: String,
            arity: Int,
        ): Rule = of(Struct.template(functor, arity), Var.anonymous())
    }
}
