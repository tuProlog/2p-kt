package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.Clause.Companion.bodyWellFormedVisitor
import it.unibo.tuprolog.core.Clause.Companion.of
import it.unibo.tuprolog.core.Terms.CLAUSE_FUNCTOR

internal abstract class ClauseImpl(
    override val head: Struct?,
    override val body: Term,
    tags: Map<String, Any>
) : StructImpl(CLAUSE_FUNCTOR, (if (head === null) arrayOf(body) else arrayOf(head, body)), tags), Clause {

    override val isWellFormed: Boolean by lazy { body.accept(bodyWellFormedVisitor) }

    override val functor: String = super<Clause>.functor

    override val args: Array<Term> by lazy { super<StructImpl>.args }

    override fun toString(): String =
        when (head) {
            null -> "$functor $body"
            else -> "$head $functor $body"
        }

    abstract override fun copyWithTags(tags: Map<String, Any>): Clause

    override fun freshCopy(): Clause = super.freshCopy() as Clause

    override fun freshCopy(scope: Scope): Clause = super.freshCopy(scope) as Clause

    override val bodyItems: Iterable<Term>
        get() = when (val body = body) {
            is Tuple -> body.toSequence()
            else -> sequenceOf(body)
        }.asIterable()

    override fun setHead(head: Struct): Rule = Rule.of(head, body)

    override fun setBody(body: Term): Clause = of(head, body)

    override fun setHeadFunctor(functor: String): Clause = of(head?.setFunctor(functor), body)

    override fun setHeadArgs(vararg arguments: Term): Clause = of(head?.setArgs(*arguments), body)

    override fun setHeadArgs(arguments: Iterable<Term>): Clause = of(head?.setArgs(arguments), body)

    override fun setHeadArgs(arguments: Sequence<Term>): Clause = of(head?.setArgs(arguments), body)

    override fun insertHeadArg(index: Int, argument: Term): Clause = of(head?.insertAt(index, argument), body)

    override fun addFirstHeadArg(argument: Term): Clause = of(head?.addFirst(argument), body)

    override fun addLastHeadArg(argument: Term): Clause = of(head?.addLast(argument), body)

    override fun appendHeadArg(argument: Term): Clause = addLastHeadArg(argument)

    override fun setBodyItems(argument: Term, vararg arguments: Term): Clause = of(head, argument, *arguments)

    override fun setBodyItems(arguments: Iterable<Term>): Clause = TODO()

    override fun setBodyItems(arguments: Sequence<Term>): Clause = TODO()

    override fun insertBodyItem(index: Int, argument: Term): Clause {
        TODO("Not yet implemented")
    }

    override fun addFirstBodyItem(argument: Term): Clause {
        TODO("Not yet implemented")
    }

    override fun addLastBodyItem(argument: Term): Clause {
        TODO("Not yet implemented")
    }

    override fun appendBodyItem(argument: Term): Clause {
        TODO("Not yet implemented")
    }
}
