package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term

@Suppress("EqualsOrHashCode")
internal abstract class TermImpl(override val tags: Map<String, Any> = emptyMap()) : Term {

    protected abstract val hashCodeCache: Int

    final override fun hashCode(): Int = hashCodeCache

    override fun equals(other: Term, useVarCompleteName: Boolean): Boolean =
        throw NotImplementedError("Subclasses should override this method")

    override fun toString(): String =
        throw NotImplementedError("Subclasses should override this method")

    override fun freshCopy(): Term = this

    override fun freshCopy(scope: Scope): Term = this
}
