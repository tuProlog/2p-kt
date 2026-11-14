package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor

internal class DirectiveImpl(
    override val body: Term,
    tags: Map<String, Any> = emptyMap(),
) : ClauseImpl(null, body, tags),
    Directive {
    override val head: Struct? = super<Directive>.head

    override fun copyWithTags(tags: Map<String, Any>): Directive = DirectiveImpl(body, tags)

    override fun freshCopy(): Directive = super.freshCopy().castToDirective()

    override fun freshCopy(scope: Scope): Directive = super.freshCopy(scope).castToDirective()

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitDirective(this)
}
