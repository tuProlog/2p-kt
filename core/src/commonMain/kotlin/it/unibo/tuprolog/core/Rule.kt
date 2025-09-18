package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.RuleImpl
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

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

    @JsName("headArgs")
    val headArgs: Iterable<Term>

    @JsName("headArity")
    val headArity: Int

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
        const val FUNCTOR = Terms.CLAUSE_FUNCTOR

        @JvmStatic
        @JsName("of")
        fun of(
            head: Struct,
            vararg body: Term,
        ): Rule = of(head, body.asIterable())

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

        @JvmStatic
        @JsName("ofSequence")
        fun of(
            head: Struct,
            body: Sequence<Term>,
        ): Rule = of(head, body.asIterable())

        @JvmStatic
        @JsName("template")
        fun template(
            functor: String,
            arity: Int,
        ): Rule = of(Struct.template(functor, arity), Var.anonymous())
    }
}
