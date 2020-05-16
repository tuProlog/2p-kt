package it.unibo.tuprolog.collections.rete.custom

import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.mergeSequences

internal object Utils {

    fun Struct.arityOfNestedFirstArgument(nestingLevel: Int): Int =
        this.firstArguments().drop(nestingLevel).first().let {
            (it as Struct).arity
        }

    fun Struct.functorOfNestedFirstArgument(nestingLevel: Int): String =
        this.firstArguments().drop(nestingLevel).first().let {
            (it as Struct).functor
        }

    fun Struct.nestedFirstArgument(nestingLevel: Int): Term =
        this.firstArguments().drop(nestingLevel).first()

    fun Struct.firstArguments(): Sequence<Term> =
        sequence {
            var currentTerm: Term = this@firstArguments
            while (currentTerm is Struct) {
                yield(currentTerm)
                currentTerm = currentTerm[0]
            }
            yield(currentTerm)
        }

    fun merge(vararg args: Sequence<SituatedIndexedClause>): Sequence<SituatedIndexedClause> =
        merge(listOf(*args))

    fun merge(sequence: Sequence<Sequence<SituatedIndexedClause>>): Sequence<SituatedIndexedClause> =
        merge(sequence.asIterable())

    fun merge(iterable: Iterable<Sequence<SituatedIndexedClause>>): Sequence<SituatedIndexedClause> =
        mergeSequences(iterable, SituatedIndexedClause::compareTo)

    fun flattenIndexed(vararg args: Sequence<SituatedIndexedClause>): Sequence<SituatedIndexedClause> =
        flattenIndexed(sequenceOf(*args))

    fun flattenIndexed(sequence: Sequence<Sequence<SituatedIndexedClause>>): Sequence<SituatedIndexedClause> =
        sequence.flatten()

    fun flattenIndexed(iterable: Iterable<Sequence<SituatedIndexedClause>>): Sequence<SituatedIndexedClause> =
        flattenIndexed(iterable.asSequence())

    fun flatten(vararg args: Sequence<Clause>): Sequence<Clause> =
        flatten(sequenceOf(*args))

    fun flatten(sequence: Sequence<Sequence<Clause>>): Sequence<Clause> =
        sequence.flatten()

    fun flatten(iterable: Iterable<Sequence<Clause>>): Sequence<Clause> =
        flatten(iterable.asSequence())

    fun comparePriority(a: SituatedIndexedClause?, b: SituatedIndexedClause?): SituatedIndexedClause? =
        when {
            a == null && b == null -> null
            a == null -> b
            b == null -> a
            a < b -> a
            else -> b
        }

}