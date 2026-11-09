package it.unibo.tuprolog.solve.search

import kotlin.js.JsName
import kotlin.jvm.JvmField

/**
 * Chooses which suspended branch the search advances next (#1010), when passed via
 * [it.unibo.tuprolog.solve.SolveOptions.setBranchScheduler]. [DEPTH_FIRST], the default, is standard Prolog.
 *
 * With any other scheduler, a supporting solver keeps every alternative as a branch of its own, with its own bindings
 * (copied when the branch is created), and asks the scheduler after each resolution step, so that an infinite branch
 * doesn't starve the others; the query has no more answers once no branch is left. Answers may come in another order,
 * or terminate where depth-first search doesn't. Solvers may only support a pure fragment of Prolog this way:
 * `:solve-prolog`'s engine rejects cut, control constructs, meta-calls and impure built-ins.
 */
fun interface BranchScheduler {
    /** The index, in [frontier], of the branch to advance next. */
    @JsName("select")
    fun select(frontier: List<Branch>): Int

    companion object {
        /** The newest branch: depth-first search, as in standard Prolog. */
        @JvmField
        val DEPTH_FIRST: BranchScheduler = BranchScheduler { it.lastIndex }

        /** The oldest branch: breadth-first search. */
        @JvmField
        val BREADTH_FIRST: BranchScheduler = BranchScheduler { 0 }
    }
}
