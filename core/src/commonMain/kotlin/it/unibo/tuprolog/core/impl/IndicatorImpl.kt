package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.core.Terms.INDICATOR_FUNCTOR

/**
 * [Indicator] default implementation
 *
 * @author Enrico
 */
internal class IndicatorImpl(
    override val nameTerm: Term,
    override val arityTerm: Term,
    tags: Map<String, Any> = emptyMap(),
) : AbstractStruct(INDICATOR_FUNCTOR, listOf(nameTerm, arityTerm), tags),
    Indicator {
    override val functor: String
        get() = INDICATOR_FUNCTOR

    override val indicatedName: String? get() = super.indicatedName

    override val indicatedArity: Int? get() = super.indicatedArity

    override fun toString(): String = "$nameTerm${Indicator.FUNCTOR}$arityTerm"

    override fun copyWithTags(tags: Map<String, Any>): Indicator = IndicatorImpl(nameTerm, arityTerm, tags)

    override fun freshCopy(): Indicator = super.freshCopy().castToIndicator()

    override fun freshCopy(scope: Scope): Indicator = super.freshCopy(scope).castToIndicator()

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitIndicator(this)
}
