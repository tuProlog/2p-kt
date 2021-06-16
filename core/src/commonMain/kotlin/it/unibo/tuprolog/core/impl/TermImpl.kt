package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.exception.SubstitutionApplicationException

@Suppress("EqualsOrHashCode")
internal abstract class TermImpl(override val tags: Map<String, Any> = emptyMap()) : Term {

    protected abstract val hashCodeCache: Int

    final override fun hashCode(): Int = hashCodeCache

    abstract override fun equals(other: Term, useVarCompleteName: Boolean): Boolean

    abstract override fun toString(): String

    override fun freshCopy(): Term = this

    override fun freshCopy(scope: Scope): Term = this

    final override fun replaceTags(tags: Map<String, Any>): Term =
        if (this.tags != tags) {
            copyWithTags(tags)
        } else {
            this
        }

    protected abstract fun copyWithTags(tags: Map<String, Any>): Term

    override fun apply(substitution: Substitution): Term  = when {
        substitution is Substitution.Unifier -> {
            if (isUnifierSkippable(substitution)) {
                this
            } else {
                applyNonEmptyUnifier(substitution)
            }
        }
        else -> throw SubstitutionApplicationException(this, substitution)
    }

    protected open fun isUnifierSkippable(unifier: Substitution.Unifier): Boolean = unifier.isEmpty() || this.isGround

    protected open fun applyNonEmptyUnifier(unifier: Substitution.Unifier): Term = this
}
