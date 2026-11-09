package it.unibo.tuprolog.collections

import it.unibo.tuprolog.core.Clause
import kotlin.js.JsName

/**
 * A [ClauseCollection] that can be changed in place. Lookups and iterations follow the *logical update view*: they
 * see the clauses as of when they started, whatever changes happen while they are consumed.
 */
interface MutableClauseCollection : ClauseCollection {
    /** Adds [clause] (at the end, if ordered); returns whether the collection changed. */
    @JsName("add")
    fun add(clause: Clause): Boolean

    /** Adds all [clauses] (at the end and in order, if ordered); returns whether the collection changed. */
    @JsName("addAll")
    fun addAll(clauses: Iterable<Clause>): Boolean = clauses.fold(false) { changed, it -> add(it) || changed }

    /** Removes the first clause unifying with [query], returning it, or `null` if none does. */
    @JsName("removeFirstMatching")
    fun removeFirstMatching(query: Clause): Clause?

    /** Removes all the clauses unifying with [query], returning them (empty if none does). */
    @JsName("removeAllMatching")
    fun removeAllMatching(query: Clause): List<Clause>

    /** Removes all clauses. */
    @JsName("clear")
    fun clear()
}
