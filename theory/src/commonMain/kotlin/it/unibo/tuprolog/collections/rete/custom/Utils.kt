package it.unibo.tuprolog.collections.rete.custom

import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause
import it.unibo.tuprolog.collections.rete.takeFirstAfterSkipping
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.utils.mergeSequences

internal object Utils {

    /**Calculate the arity of the first argument of any [Struct], at the given nesting level.
     * No checks are performed upon the validity of the Struct this extension method is called upon. */
    fun Struct.arityOfNestedFirstArgument(nestingLevel: Int): Int =
        this.firstArguments().takeFirstAfterSkipping(nestingLevel).let { it.castToStruct().arity }

    /**Calculate the functor of the first argument of any [Struct], at the given nesting level.
     * No checks are performed upon the validity of the Struct this extension method is called upon. */
    fun Struct.functorOfNestedFirstArgument(nestingLevel: Int): String =
        this.firstArguments().takeFirstAfterSkipping(nestingLevel).let { it.castToStruct().functor }

    /**Calculate the [Term] representing the first argument of any [Struct], at the given nesting level.
     * No checks are performed upon the validity of the Struct this extension method is called upon. */
    fun Struct.nestedFirstArgument(nestingLevel: Int): Term =
        this.firstArguments().takeFirstAfterSkipping(nestingLevel)

    private fun Struct.firstArguments(): Sequence<Term> =
        sequence {
            var currentTerm: Term = this@firstArguments
            while (currentTerm.isStruct) {
                yield(currentTerm)
                currentTerm = currentTerm.castToStruct().getArgAt(0)
            }
            yield(currentTerm)
        }

    /**Sorts all the given [Sequence] of [SituatedIndexedClause]*/
    fun merge(vararg args: Sequence<SituatedIndexedClause>): Sequence<SituatedIndexedClause> =
        merge(listOf(*args))

    /**Sorts all the given [Sequence] of [SituatedIndexedClause]*/
    fun merge(sequence: Sequence<Sequence<SituatedIndexedClause>>): Sequence<SituatedIndexedClause> =
        merge(sequence.asIterable())

    /**Sorts all the given [Sequence] of [SituatedIndexedClause]*/
    fun merge(iterable: Iterable<Sequence<SituatedIndexedClause>>): Sequence<SituatedIndexedClause> =
        mergeSequences(iterable, SituatedIndexedClause::compareTo)

    /**Composes all the given [Sequence] of [SituatedIndexedClause], disregarding order*/
    fun flattenIndexed(vararg args: Sequence<SituatedIndexedClause>): Sequence<SituatedIndexedClause> =
        flattenIndexed(sequenceOf(*args))

    /**Composes all the given [Sequence] of [SituatedIndexedClause], disregarding order*/
    fun flattenIndexed(sequence: Sequence<Sequence<SituatedIndexedClause>>): Sequence<SituatedIndexedClause> =
        sequence.flatten()

    /**Composes all the given [Sequence] of [SituatedIndexedClause], disregarding order*/
    fun flattenIndexed(iterable: Iterable<Sequence<SituatedIndexedClause>>): Sequence<SituatedIndexedClause> =
        flattenIndexed(iterable.asSequence())

    /**Composes all the given [Sequence] of [Clause], disregarding order*/
    fun flatten(vararg args: Sequence<Clause>): Sequence<Clause> =
        flatten(sequenceOf(*args))

    /**Composes all the given [Sequence] of [Clause], disregarding order*/
    fun flatten(sequence: Sequence<Sequence<Clause>>): Sequence<Clause> =
        sequence.flatten()

    /**Composes all the given [Sequence] of [Clause], disregarding order*/
    fun flatten(iterable: Iterable<Sequence<Clause>>): Sequence<Clause> =
        flatten(iterable.asSequence())

    /**Compares two nullable [SituatedIndexedClause]. If both are null, null is returned*/
    fun comparePriority(a: SituatedIndexedClause?, b: SituatedIndexedClause?): SituatedIndexedClause? =
        when {
            a == null && b == null -> null
            a == null -> b
            b == null -> a
            a < b -> a
            else -> b
        }
}
