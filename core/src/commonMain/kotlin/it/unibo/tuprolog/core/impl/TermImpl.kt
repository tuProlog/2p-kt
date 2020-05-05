package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Term

@Suppress("EqualsOrHashCode")
internal abstract class TermImpl : Term {
    protected abstract val hashCode: Int

    final override fun hashCode(): Int = hashCode

    override fun equals(other: Term, useVarCompleteName: Boolean): Boolean =
        throw NotImplementedError("Subclasses should override this method")

    override fun toString(): String =
        throw NotImplementedError("Subclasses should override this method")
}
