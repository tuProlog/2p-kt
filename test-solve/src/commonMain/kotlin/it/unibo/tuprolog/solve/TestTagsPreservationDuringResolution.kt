package it.unibo.tuprolog.solve

/**
 * Verifies that term tags (see `it.unibo.tuprolog.utils.Taggable`) attached to a theory's clauses, or to a query,
 * survive resolution and are visible to a custom `it.unibo.tuprolog.unify.Unificator`.
 *
 * All test cases share the same base theory clause `f(g(x)) :- true.` and query `f(g(x))`, with tags optionally
 * attached at one of four depths: on the whole clause (`CLAUSE`), on the `f(...)` struct (`F`), on the `g(...)`
 * struct (`G`), or on the `x` atom itself (`X`).
 *
 * Cases are checked against two comparison strategies (see the `TagComparisonVerse` used by the implementation):
 * `EQUALS`, which requires the tags on both sides of a unification equation to be identical for it to hold, and
 * `IGNORE`, which disregards tags entirely.
 *
 * A note on what these tests actually exercise: `Equation.allOf` (in `it.unibo.tuprolog.unify.Equation`) never
 * creates an equation for a *matching* struct-vs-struct pair (same functor/arity); it always decomposes such a
 * pair directly into per-argument equations. As a consequence, tags attached to an intermediate struct (`CLAUSE`,
 * `F`, `G`) are never observed by [it.unibo.tuprolog.unify.AbstractUnificator.handleEquation]: only tags on a leaf
 * term (an atom, a number, a variable, or a mismatched struct) reach it. So, of the four depths, only `X` actually
 * discriminates under `EQUALS`; `CLAUSE`, `F` and `G` behave identically to an untagged clause. This is intentional
 * documentation of current framework behavior, not an oversight in the tests: [testUntaggedQueryAgainstTaggedTheory]
 * and [testQueryTaggedDifferentlyFromTheory] assert exactly this shape of result.
 */
interface TestTagsPreservationDuringResolution : SolverTest {
    companion object {
        fun prototype(solverFactory: SolverFactory): TestTagsPreservationDuringResolution =
            TestTagsPreservationDuringResolutionImpl(solverFactory)
    }

    /**
     * Case 1: the theory clause is tagged (at each of the four depths), and the query is *not*.
     *
     * Under `IGNORE`, resolution always succeeds (tags play no role). Under `EQUALS`, it only fails when the tag
     * is actually observable, i.e. at depth `X`, since an empty query-side tag map differs from the clause's;
     * at `CLAUSE`/`F`/`G` the tag is unreachable during unification, so resolution succeeds regardless.
     */
    fun testUntaggedQueryAgainstTaggedTheory()

    /**
     * Case 2: the query is tagged exactly like the theory clause, at the same depth (`F`, `G` or `X`; `CLAUSE` has
     * no query counterpart, since a query is a plain goal, not a wrapping clause).
     *
     * Resolution always succeeds, with exactly one solution, under both `EQUALS` and `IGNORE`.
     */
    fun testQueryTaggedLikeTheory()

    /**
     * Case 3: the query is tagged differently from the theory clause, at the same depth (`F`, `G` or `X`) — once
     * with a different tag value for the same key, once with a different key altogether.
     *
     * Under `IGNORE`, resolution still succeeds. Under `EQUALS`, it only fails where the mismatch is actually
     * observable, i.e. at depth `X`; at `F`/`G` the tag is unreachable, so resolution succeeds regardless, exactly
     * as it would with a fully untagged query (see [testUntaggedQueryAgainstTaggedTheory]).
     */
    fun testQueryTaggedDifferentlyFromTheory()

    /**
     * Case 4: exercises a small recursive theory (a hand-rolled `count/2`, walking a hand-rolled list of nodes
     * `l/2` terminated by `e`, and building up a Peano-style `succ/1`-wrapped `zero` result) whose resolution goes
     * through several goal-selection/rule-selection/rule-execution/backtracking cycles, so that a tag can flow
     * through multiple unification steps.
     *
     * Every atom contributed by the query (`a`, `b`, `c`) is tagged as goal-originated; every atom contributed by
     * the theory (`zero`, and each `succ` wrapper) is tagged as theory-originated. A custom
     * `it.unibo.tuprolog.unify.Unificator.handleEquation` inspects every equation handled during resolution and
     * fails the test if a goal-originated tag is ever found on the right-hand side of an equation, or a
     * theory-originated tag is ever found on its left-hand side — since resolution always unifies the current goal
     * (lhs) against a clause head (rhs), a correct implementation should never mix the two. It also fails if
     * neither kind of tag was ever observed at all, which would indicate tags silently disappeared during
     * resolution rather than legitimately never crossing sides.
     *
     * This test is meant to help a developer pinpoint *where*, during resolution, a tag gets lost or misplaced.
     */
    fun testTagOriginDuringComputation()
}
