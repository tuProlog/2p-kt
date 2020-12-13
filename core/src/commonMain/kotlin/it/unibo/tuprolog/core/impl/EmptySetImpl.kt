package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.EmptySet
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var

internal class EmptySetImpl(
    tags: Map<String, Any> = emptyMap()
) : SetImpl(null, tags), EmptySet {

    override val args: Array<Term> by lazy { super<EmptySet>.args }

    override val argsList: List<Term> by lazy { super<SetImpl>.argsList }

    override val functor: String = super<EmptySet>.functor

    override val isGround: Boolean by lazy { super<EmptySet>.isGround }

    override val size: Int get() = 0

    override val variables: Sequence<Var> by lazy { super<EmptySet>.variables }

    override fun replaceTags(tags: Map<String, Any>): EmptySet {
        return EmptySetImpl(tags)
    }

    override fun freshCopy(): EmptySet = this

    override fun freshCopy(scope: Scope): EmptySet = this
}
