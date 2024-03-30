package it.unibo.tuprolog.core

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
            vararg goals: Term,
        ): Rule = TermFactory.default.ruleOf(head, *goals)

        @JvmStatic
        @JsName("ofIterable")
        fun of(
            head: Struct,
            body: Iterable<Term>,
        ): Rule = TermFactory.default.ruleOf(head, body)

        @JvmStatic
        @JsName("ofSequence")
        fun of(
            head: Struct,
            body: Sequence<Term>,
        ): Rule = TermFactory.default.ruleOf(head, body)

        @JvmStatic
        @JsName("template")
        fun template(
            functor: String,
            arity: Int,
        ): Rule = TermFactory.default.ruleTemplateOf(functor, arity)
    }
}
