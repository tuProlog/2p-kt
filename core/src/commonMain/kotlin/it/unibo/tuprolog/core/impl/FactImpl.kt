package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.core.Truth

internal class FactImpl(
    override val head: Struct,
    tags: Map<String, Any> = emptyMap(),
) : RuleImpl(head, Truth.TRUE, tags),
    Fact {
    override val isWellFormed: Boolean = true

    override val body: Term = super<RuleImpl>.body

    override fun copyWithTags(tags: Map<String, Any>): Fact = FactImpl(head, tags)

    override fun freshCopy(): Fact = super.freshCopy().castToFact()

    override fun freshCopy(scope: Scope): Fact = super.freshCopy(scope).castToFact()

    override fun setHeadFunctor(functor: String): Fact = super.setHeadFunctor(functor).castToFact()

    override fun setHeadArgs(vararg arguments: Term): Fact = super.setHeadArgs(*arguments).castToFact()

    override fun setHeadArgs(arguments: Iterable<Term>): Fact = super.setHeadArgs(arguments).castToFact()

    override fun setHeadArgs(arguments: Sequence<Term>): Fact = super.setHeadArgs(arguments).castToFact()

    override fun insertHeadArg(
        index: Int,
        argument: Term,
    ): Fact = super.insertHeadArg(index, argument).castToFact()

    override fun addFirstHeadArg(argument: Term): Fact = super.addFirstHeadArg(argument).castToFact()

    override fun addLastHeadArg(argument: Term): Fact = super.addLastHeadArg(argument).castToFact()

    override fun appendHeadArg(argument: Term): Fact = super.appendHeadArg(argument).castToFact()

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitFact(this)
}
