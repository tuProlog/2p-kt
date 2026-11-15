package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Clause.Companion.bodyWellFormedVisitor
import it.unibo.tuprolog.core.Clause.Companion.of
import it.unibo.tuprolog.core.Compound
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.TermVisitor
import it.unibo.tuprolog.core.Terms.CLAUSE_FUNCTOR
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.utils.insertAt

internal abstract class ClauseImpl(
    override val head: Compound?,
    override val body: Term,
    tags: Map<String, Any>,
) : AbstractStruct(CLAUSE_FUNCTOR, (if (head === null) listOf(body) else listOf(head, body)), tags),
    Clause {
    // read through `.value`, not `by`: on JS, delegation builds a property reference at every read
    private val lazyIsWellFormed = lazy { body.accept(bodyWellFormedVisitor) }

    override val isWellFormed: Boolean
        get() = lazyIsWellFormed.value

    override val functor: String = super<Clause>.functor

    override fun toString(): String =
        when (head) {
            null -> "$functor $body"
            else -> "$head $functor $body"
        }

    abstract override fun copyWithTags(tags: Map<String, Any>): Clause

    override fun freshCopy(): Clause = super.freshCopy().castToClause()

    override fun freshCopy(scope: Scope): Clause = super.freshCopy(scope).castToClause()

    private val bodyItemsSequence: Sequence<Term>
        get() =
            body.let {
                when {
                    it.isTuple -> it.castToTuple().toSequence()
                    else -> sequenceOf(it)
                }
            }

    override val bodyItems: Iterable<Term>
        get() = bodyItemsSequence.asIterable()

    override val bodySize: Int
        get() =
            body.let {
                when {
                    it.isTuple -> it.castToTuple().size
                    else -> 1
                }
            }

    override val bodyAsTuple: Tuple?
        get() = body.asTuple()

    private fun ensureIndexIsInBodyRange(index: Int) {
        val size = bodySize
        if (index < 0 || index >= size) {
            throw IndexOutOfBoundsException("Attempt to access body item $index of a clause having $size body items")
        }
    }

    override fun getBodyItem(index: Int): Term {
        ensureIndexIsInBodyRange(index)
        return bodyItemsSequence.drop(index).first()
    }

    override fun setHead(head: Compound): Rule = Rule.of(head, body)

    override fun setBody(body: Term): Clause = of(head, body)

    override fun setHeadFunctor(functor: String): Clause = of(head?.let { Struct.of(functor, it.args) }, body)

    override fun setHeadArgs(vararg arguments: Term): Clause = of(head?.setArgs(*arguments), body)

    override fun setHeadArgs(arguments: Iterable<Term>): Clause = of(head?.setArgs(arguments), body)

    override fun setHeadArgs(arguments: Sequence<Term>): Clause = of(head?.setArgs(arguments), body)

    // rebuilding through Compound.of keeps a Pattern head a Pattern, and lets a Struct head be specialised
    private inline fun withHeadArgs(newArgs: (List<Term>) -> List<Term>): Clause =
        of(head?.let { Compound.of(it.functor, newArgs(it.args)) }, body)

    override fun insertHeadArg(
        index: Int,
        argument: Term,
    ): Clause =
        withHeadArgs { args ->
            if (index !in args.indices) throw IndexOutOfBoundsException("Index $index is out of bounds ${args.indices}")
            args.subList(0, index) + argument + args.subList(index, args.size)
        }

    override fun addFirstHeadArg(argument: Term): Clause = withHeadArgs { listOf(argument) + it }

    override fun addLastHeadArg(argument: Term): Clause = withHeadArgs { it + argument }

    override fun appendHeadArg(argument: Term): Clause = addLastHeadArg(argument)

    override fun setBodyItems(
        argument: Term,
        vararg arguments: Term,
    ): Clause = of(head, argument, *arguments)

    override fun setBodyItems(arguments: Iterable<Term>): Clause = of(head, arguments)

    override fun setBodyItems(arguments: Sequence<Term>): Clause = of(head, arguments)

    override fun insertBodyItem(
        index: Int,
        argument: Term,
    ): Clause {
        ensureIndexIsInBodyRange(index)
        return of(head, bodyItemsSequence.insertAt(index, argument))
    }

    override fun addFirstBodyItem(argument: Term): Clause = of(head, sequenceOf(argument) + bodyItemsSequence)

    override fun addLastBodyItem(argument: Term): Clause = of(head, bodyItemsSequence + sequenceOf(argument))

    override fun appendBodyItem(argument: Term): Clause = addLastBodyItem(argument)

    override fun <T> accept(visitor: TermVisitor<T>): T = visitor.visitClause(this)
}
