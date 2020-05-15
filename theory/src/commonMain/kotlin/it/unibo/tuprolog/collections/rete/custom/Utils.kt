package it.unibo.tuprolog.collections.rete.nodes.custom

import it.unibo.tuprolog.collections.rete.nodes.custom.clause.SituatedIndexedClause
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term

internal object Utils {

    fun Struct.arityOfNestedFirstArgument(nestingLevel: Int): Int {
        this.firstArguments().drop(nestingLevel).first().let {
            return (it as Struct).arity
        }
    }

    fun Struct.functorOfNestedFirstArgument(nestingLevel: Int): String =
        this.firstArguments().drop(nestingLevel).first().let {
            return (it as Struct).functor
        }


    fun Struct.nestedFirstArgument(nestingLevel: Int): Term =
        this.firstArguments().drop(nestingLevel).first()

    private fun Struct.firstArguments(): Sequence<Term> {
        return sequence {
            var currentTerm: Term = this@firstArguments
            while (currentTerm is Struct) {
                yield(currentTerm)
                currentTerm = currentTerm[0]
            }
            yield(currentTerm)
        }
    }

    fun mergeSort(vararg args: Sequence<SituatedIndexedClause>): Sequence<SituatedIndexedClause> {
        TODO()
    }

    fun mergeSort(sequence: Sequence<Sequence<SituatedIndexedClause>>): Sequence<SituatedIndexedClause> {
        TODO()
    }

    fun mergeSort(iterable: Iterable<Sequence<SituatedIndexedClause>>): Sequence<SituatedIndexedClause> {
        TODO()
    }

    fun merge(vararg args: Sequence<SituatedIndexedClause>): Sequence<SituatedIndexedClause> =
        merge(*args)

    fun merge(sequence: Sequence<Sequence<SituatedIndexedClause>>): Sequence<SituatedIndexedClause> =
        merge(sequence.asIterable())

    fun merge(iterable: Iterable<Sequence<SituatedIndexedClause>>): Sequence<SituatedIndexedClause> =
        iterable.flatMap{it.toList()}.asSequence()

    fun merge(vararg args: Sequence<Clause>): Sequence<Clause> =
        merge(*args)

    fun merge(sequence: Sequence<Sequence<Clause>>): Sequence<Clause> =
        merge(sequence.asIterable())

    fun merge(iterable: Iterable<Sequence<Clause>>): Sequence<Clause> =
        iterable.flatMap{it.toList()}.asSequence()

    fun comparePriority(a: SituatedIndexedClause?, b: SituatedIndexedClause?): SituatedIndexedClause? =
        when {
            a == null && b == null -> null
            a == null -> b
            b == null -> a
            a < b -> a
            else -> b
        }

}