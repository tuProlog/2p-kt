package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.impl.ReteClauseMultiSet
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.TermComparator
import it.unibo.tuprolog.utils.itemWiseEquals
import it.unibo.tuprolog.utils.itemWiseHashCode
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

interface ClauseMultiSet : ClauseCollection {

    /** Gives the number of [Clause] that would unify over the given clause. **/
    @JsName("count")
    fun count(clause: Clause): Long

    /** Produces a [Sequence] of the clauses that would unify over the given [Clause]. **/
    operator fun get(clause: Clause): Sequence<Clause>

    /** Gives a freshly produced [ClauseMultiSet] including the given [Clause] and the content of this one **/
    override fun add(clause: Clause): ClauseMultiSet

    /** Gives a freshly produced [ClauseMultiSet] including all the given [Clause] and the content of this one **/
    override fun addAll(clauses: Iterable<Clause>): ClauseMultiSet

    /** Produces a [RetrieveResult] as a consequence of the attempt at deleting the given [Clause]
     *  from this [ClauseMultiSet] **/
    override fun retrieve(clause: Clause): RetrieveResult<out ClauseMultiSet>

    /** Produces a [RetrieveResult] as a consequence of the attempt at deleting all the given [Clause]
     *  from this [ClauseMultiSet] **/
    override fun retrieveAll(clause: Clause): RetrieveResult<out ClauseMultiSet>

    companion object {

        /** Creates an empty [ClauseMultiSet] **/
        @JvmStatic
        @JsName("empty")
        fun empty(): ClauseMultiSet = of(emptyList())

        /** Creates a [ClauseMultiSet] with given clauses */
        @JvmStatic
        @JsName("of")
        fun of(vararg clause: Clause): ClauseMultiSet = of(clause.asIterable())

        /** Let developers easily create a [ClauseMultiSet] programmatically while avoiding variables names clashing */
        @JvmStatic
        @JsName("ofScopes")
        fun of(vararg clause: Scope.() -> Clause): ClauseMultiSet =
            of(clause.map {
                Scope.empty(it)
            })

        /** Creates a [ClauseMultiSet] from the given [Sequence] of [Clause] */
        @JvmStatic
        @JsName("ofSequence")
        fun of(clauses: Sequence<Clause>): ClauseMultiSet = of(clauses.asIterable())

        /** Creates a [ClauseMultiSet] from the given [Iterable] of [Clause] */
        @JvmStatic
        @JsName("ofIterable")
        fun of(clauses: Iterable<Clause>): ClauseMultiSet =
            ReteClauseMultiSet(clauses)

        @JvmStatic
        @JsName("equals")
        fun equals(multiSet1: ClauseMultiSet, multiSet2: ClauseMultiSet): Boolean {
            return itemWiseEquals(
                multiSet1.sortedWith(TermComparator.DefaultComparator),
                multiSet2.sortedWith(TermComparator.DefaultComparator)
            )
        }

        @JvmStatic
        @JsName("hashCode")
        fun hashCode(multiSet: ClauseMultiSet): Int {
            return itemWiseHashCode(
                ClauseMultiSet::class,
                multiSet.sortedWith(TermComparator.DefaultComparator)
            )
        }
    }
}

