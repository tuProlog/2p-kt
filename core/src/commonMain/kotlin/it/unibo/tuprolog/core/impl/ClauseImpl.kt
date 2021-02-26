package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Clause.Companion.bodyWellFormedVisitor
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
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
}
