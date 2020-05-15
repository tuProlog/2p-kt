package it.unibo.tuprolog.collections.rete.custom

import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term

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

    private fun Struct.firstArguments(): Sequence<Term> =
        sequence {
            var currentTerm: Term = this@firstArguments
            while (currentTerm is Struct) {
                yield(currentTerm)
                currentTerm = currentTerm[0]
            }
            yield(currentTerm)
        }

    fun mergeSort(vararg args: Sequence<SituatedIndexedClause>): Sequence<SituatedIndexedClause> =
        mergeSort(listOf(*args))

    fun mergeSort(sequence: Sequence<Sequence<SituatedIndexedClause>>): Sequence<SituatedIndexedClause> =
        mergeSort(sequence.asIterable())

    fun mergeSort(iterable: Iterable<Sequence<SituatedIndexedClause>>): Sequence<SituatedIndexedClause> {
        TODO("not implemented")
    }

    fun merge(vararg args: Sequence<SituatedIndexedClause>): Sequence<SituatedIndexedClause> =
        merge(sequenceOf(*args))

    fun merge(sequence: Sequence<Sequence<SituatedIndexedClause>>): Sequence<SituatedIndexedClause> =
        sequence.flatten()

    fun merge(iterable: Iterable<Sequence<SituatedIndexedClause>>): Sequence<SituatedIndexedClause> =
        merge(iterable.asSequence())

    fun merge(vararg args: Sequence<Clause>): Sequence<Clause> =
        merge(sequenceOf(*args))

    fun merge(sequence: Sequence<Sequence<Clause>>): Sequence<Clause> =
        sequence.flatten()

    fun merge(iterable: Iterable<Sequence<Clause>>): Sequence<Clause> =
        merge(iterable.asSequence())

    fun comparePriority(a: SituatedIndexedClause?, b: SituatedIndexedClause?): SituatedIndexedClause? =
        when {
            a == null && b == null -> null
            a == null -> b
            b == null -> a
            a < b -> a
            else -> b
        }

}