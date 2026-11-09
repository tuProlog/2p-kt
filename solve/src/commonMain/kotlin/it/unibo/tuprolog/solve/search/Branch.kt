package it.unibo.tuprolog.solve.search

import it.unibo.tuprolog.core.Struct
import kotlin.js.JsName

/** A suspended branch of the search, as a [BranchScheduler] sees it (#1010). Valid only during [BranchScheduler.select]. */
interface Branch {
    /** The branch's id, the same as long as the branch lives. */
    @JsName("id")
    val id: Long

    /** The branch's proof depth: the clauses resolved from the query, regardless of last-call optimisation. */
    @JsName("depth")
    val depth: Int

    /** The goals the branch has left, with its own bindings applied. */
    @JsName("goals")
    val goals: List<Struct>
}
