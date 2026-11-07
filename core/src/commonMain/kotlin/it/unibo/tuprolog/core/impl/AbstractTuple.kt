package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.core.Terms.TUPLE_FUNCTOR
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.core.TupleIterator

internal abstract class AbstractTuple(
    args: List<Term>,
    tags: Map<String, Any>,
) : RecursiveImpl(TUPLE_FUNCTOR, args, tags),
    Tuple {
    override val unfoldedSequence: Sequence<Term>
        get() = Iterable { TupleIterator(this) }.asSequence()

    override fun unfold(): Sequence<Term> = Iterable { TupleUnfolder(this) }.asSequence()

    // explicit, as on Kotlin/JS the interface's default implementations are not exposed under their @JsName
    override fun toList(): List<Term> = unfoldedList

    override fun toArray(): Array<Term> = unfoldedArray

    override fun toSequence(): Sequence<Term> = unfoldedSequence

    override val functor: String = TUPLE_FUNCTOR

    override fun toString(): String = unfoldedSequence.joinToString(", ", "(", ")")

    abstract override fun copyWithTags(tags: Map<String, Any>): Tuple

    override fun freshCopy(): Tuple = super.freshCopy().castToTuple()

    override fun freshCopy(scope: Scope): Tuple = super.freshCopy(scope).castToTuple()

    final override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitTuple(this)
}
