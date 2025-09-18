package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.core.Truth

internal class TruthImpl(
    value: String,
    override val isTrue: Boolean,
    tags: Map<String, Any> = emptyMap(),
) : AtomImpl(value, tags),
    Truth {
    override fun toString(): String = value

    override fun copyWithTags(tags: Map<String, Any>): Truth = TruthImpl(value, isTrue, tags)

    override fun freshCopy(): Truth = this

    override fun freshCopy(scope: Scope): Truth = this

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitTruth(this)
}
