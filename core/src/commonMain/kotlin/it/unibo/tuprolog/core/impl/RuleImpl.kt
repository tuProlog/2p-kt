package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor

internal open class RuleImpl(
    override val head: Struct,
    override val body: Term,
    tags: Map<String, Any> = emptyMap(),
) : ClauseImpl(head, body, tags),
    Rule {
    override fun copyWithTags(tags: Map<String, Any>): Rule = RuleImpl(head, body, tags)

    override fun freshCopy(): Rule = super.freshCopy().castToRule()

    override fun freshCopy(scope: Scope): Rule = super.freshCopy(scope).castToRule()

    override val headArgs: Iterable<Term>
        get() = head.args

    override val headArity: Int
        get() = head.arity

    override fun getHeadArg(index: Int): Term = head[index]

    override fun setBody(body: Term): Rule = super.setBody(body).castToRule()

    override fun setHeadFunctor(functor: String): Rule = super.setHeadFunctor(functor).castToRule()

    override fun setHeadArgs(vararg arguments: Term): Rule = super.setHeadArgs(*arguments).castToRule()

    override fun setHeadArgs(arguments: Iterable<Term>): Rule = super.setHeadArgs(arguments).castToRule()

    override fun setHeadArgs(arguments: Sequence<Term>): Rule = super.setHeadArgs(arguments).castToRule()

    override fun insertHeadArg(
        index: Int,
        argument: Term,
    ): Rule = super.insertHeadArg(index, argument).castToRule()

    override fun addFirstHeadArg(argument: Term): Rule = super.addFirstHeadArg(argument).castToRule()

    override fun addLastHeadArg(argument: Term): Rule = super.addLastHeadArg(argument).castToRule()

    override fun appendHeadArg(argument: Term): Rule = super.appendHeadArg(argument).castToRule()

    override fun setBodyItems(
        argument: Term,
        vararg arguments: Term,
    ): Rule = super.setBodyItems(argument, *arguments).castToRule()

    override fun setBodyItems(arguments: Iterable<Term>): Rule = super.setBodyItems(arguments).castToRule()

    override fun setBodyItems(arguments: Sequence<Term>): Rule = super.setBodyItems(arguments).castToRule()

    override fun insertBodyItem(
        index: Int,
        argument: Term,
    ): Rule = super.insertBodyItem(index, argument).castToRule()

    override fun addFirstBodyItem(argument: Term): Rule = super.addFirstBodyItem(argument).castToRule()

    override fun addLastBodyItem(argument: Term): Rule = super.addLastBodyItem(argument).castToRule()

    override fun appendBodyItem(argument: Term): Rule = super.appendBodyItem(argument).castToRule()

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitRule(this)
}
