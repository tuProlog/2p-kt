package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Goal
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor

internal class GoalImpl(
    override val body: Term,
    tags: Map<String, Any> = emptyMap(),
) : ClauseImpl(null, body, tags),
    Goal {
    override val head: Nothing? = super<Goal>.head

    override fun copyWithTags(tags: Map<String, Any>): Goal = GoalImpl(body, tags)

    override fun freshCopy(): Goal = super.freshCopy().castToGoal()

    override fun freshCopy(scope: Scope): Goal = super.freshCopy(scope).castToGoal()

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitGoal(this)
}
