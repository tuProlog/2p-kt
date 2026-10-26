package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.unify.AbstractUnificator
import it.unibo.tuprolog.unify.Equation
import it.unibo.tuprolog.utils.setTags
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Verifies that term tags (see `it.unibo.tuprolog.utils.Taggable`) attached to a theory's clauses, or to a query, survive resolution and are visible to a custom `it.unibo.tuprolog.unify.Unificator`. */
fun tagsPreservationDuringResolutionContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    // Case 1: the theory clause is tagged (at each of the four depths), and the query is *not*.
    //
    // Under `IGNORE`, resolution always succeeds (tags play no role). Under `EQUALS`, it only fails when the tag
    // is actually observable, i.e. at depth `X`, since an empty query-side tag map differs from the clause's;
    // at `CLAUSE`/`F`/`G` the tag is unreachable during unification, so resolution succeeds regardless.
    scenario("tags-preservation-during-resolution/untagged-query-against-tagged-theory") {
        for (depth in Depth.entries) {
            for (verse in verses) {
                val solutions = countSolutions(solverFor(depth, verse), untaggedQuery())
                // Equation.allOf never emits an equation for a *matching* struct-vs-struct pair, it always
                // decomposes straight into per-argument equations; so tags on an intermediate struct (CLAUSE, F, G)
                // are invisible to any Unificator, only a leaf's (X) tags are ever visible
                val expected = if (verse == TagComparisonVerse.IGNORE || depth != Depth.X) 1 else 0
                assertEquals(expected, solutions, "depth=$depth verse=$verse")
            }
        }
    }

    // Case 2: the query is tagged exactly like the theory clause, at the same depth (`F`, `G` or `X`; `CLAUSE` has
    // no query counterpart, since a query is a plain goal, not a wrapping clause).
    //
    // Resolution always succeeds, with exactly one solution, under both `EQUALS` and `IGNORE`.
    scenario("tags-preservation-during-resolution/query-tagged-like-theory") {
        for (depth in listOf(Depth.F, Depth.G, Depth.X)) {
            for (verse in verses) {
                val solutions = countSolutions(solverFor(depth, verse), taggedQuery(depth, tagA))
                assertEquals(1, solutions, "depth=$depth verse=$verse")
            }
        }
    }

    // Case 3: the query is tagged differently from the theory clause, at the same depth (`F`, `G` or `X`) — once
    // with a different tag value for the same key, once with a different key altogether.
    //
    // Under `IGNORE`, resolution still succeeds. Under `EQUALS`, it only fails where the mismatch is actually
    // observable, i.e. at depth `X`; at `F`/`G` the tag is unreachable, so resolution succeeds regardless, exactly
    // as it would with a fully untagged query (see [testUntaggedQueryAgainstTaggedTheory]).
    scenario("tags-preservation-during-resolution/query-tagged-differently-from-theory") {
        for (depth in listOf(Depth.F, Depth.G, Depth.X)) {
            for (differentTag in listOf(tagDifferentValue, tagDifferentKey)) {
                for (verse in verses) {
                    val solutions = countSolutions(solverFor(depth, verse), taggedQuery(depth, differentTag))
                    // see testUntaggedQueryAgainstTaggedTheory: only leaf-level (X) tags are ever visible
                    val expected = if (verse == TagComparisonVerse.IGNORE || depth != Depth.X) 1 else 0
                    assertEquals(expected, solutions, "depth=$depth tag=$differentTag verse=$verse")
                }
            }
        }
    }

    // Case 4: exercises a small recursive theory (a hand-rolled `count/2`, walking a hand-rolled list of nodes
    // `l/2` terminated by `e`, and building up a Peano-style `succ/1`-wrapped `zero` result) whose resolution goes
    // through several goal-selection/rule-selection/rule-execution/backtracking cycles, so that a tag can flow
    // through multiple unification steps.
    //
    // Every atom contributed by the query (`a`, `b`, `c`) is tagged as goal-originated; every atom contributed by
    // the theory (`zero`, and each `succ` wrapper) is tagged as theory-originated. A custom
    // `it.unibo.tuprolog.unify.Unificator.handleEquation` inspects every equation handled during resolution and
    // fails the test if a goal-originated tag is ever found on the right-hand side of an equation, or a
    // theory-originated tag is ever found on its left-hand side — since resolution always unifies the current goal
    // (lhs) against a clause head (rhs), a correct implementation should never mix the two. It also fails if
    // neither kind of tag was ever observed at all, which would indicate tags silently disappeared during
    // resolution rather than legitimately never crossing sides.
    //
    // This test is meant to help a developer pinpoint *where*, during resolution, a tag gets lost or misplaced.
    scenario("tags-preservation-during-resolution/tag-origin-during-computation") {
        val goalTag = mapOf("origin" to "goal")
        val theoryTag = mapOf("origin" to "theory")

        val theory =
            logicProgramming {
                theoryOf(
                    fact { "count"("e", atomOf("zero").setTags(theoryTag)) },
                    rule {
                        "count"("l"(A, B), "succ"(C).setTags(theoryTag)) impliedBy "count"(B, C)
                    },
                )
            }
        val unificator = TagFlowUnificator()
        val solver = solverFactory.solverWithDefaultBuiltins(staticKb = theory, unificator = unificator)
        val goal =
            logicProgramming {
                "count"(
                    "l"(
                        atomOf("a").setTags(goalTag),
                        "l"(atomOf("b").setTags(goalTag), "l"(atomOf("c").setTags(goalTag), "e")),
                    ),
                    R,
                )
            }

        val solutions = solver.solveList(goal).filter { it.isYes }

        assertEquals(1, solutions.size)
        assertTrue(unificator.violations.isEmpty(), "tags crossed sides: ${unificator.violations}")
        assertTrue(
            unificator.sawGoalOriginatedTag,
            "goal-originated tags were never observed: they may have been lost",
        )
        assertTrue(
            unificator.sawTheoryOriginatedTag,
            "theory-originated tags were never observed: they may have been lost",
        )
    }
}

/** Where, in the `f(g(x))` clause/query, a tag is attached. */
private enum class Depth { CLAUSE, F, G, X }

private val verses = listOf(TagComparisonVerse.EQUALS, TagComparisonVerse.IGNORE)

private val tagA = mapOf("k" to "v")
private val tagDifferentValue = mapOf("k" to "other")
private val tagDifferentKey = mapOf("other" to "v")

/** Builds the `f(g(x)) :- true.` fact, tagged at the given [depth]. */
private fun taggedClause(
    depth: Depth,
    tags: Map<String, Any>,
): Fact =
    logicProgramming {
        when (depth) {
            Depth.CLAUSE -> fact { "f"("g"("x")) }.setTags(tags)
            Depth.F -> fact { "f"("g"("x")).setTags(tags) }
            Depth.G -> fact { "f"("g"("x").setTags(tags)) }
            Depth.X -> fact { "f"("g"(atomOf("x").setTags(tags))) }
        }
    }

/** Builds the `f(g(x))` query, tagged at the given [depth]; [Depth.CLAUSE] has no query counterpart. */
private fun taggedQuery(
    depth: Depth,
    tags: Map<String, Any>,
): Struct =
    logicProgramming {
        when (depth) {
            Depth.CLAUSE -> error("clause-level tags have no query counterpart")
            Depth.F -> "f"("g"("x")).setTags(tags)
            Depth.G -> "f"("g"("x").setTags(tags))
            Depth.X -> "f"("g"(atomOf("x").setTags(tags)))
        }
    }

private fun untaggedQuery(): Struct = logicProgramming { "f"("g"("x")) }

private fun ContractScope.solverFor(
    depth: Depth,
    verse: TagComparisonVerse,
): Solver {
    val theory = logicProgramming { theoryOf(taggedClause(depth, tagA)) }
    return solverFactory.solverWithDefaultBuiltins(staticKb = theory, unificator = TagAwareUnificator(verse))
}

private fun countSolutions(
    solver: Solver,
    goal: Struct,
): Int = solver.solveList(goal).count { it.isYes }

private enum class TagComparisonVerse {
    EQUALS,
    IGNORE,
}

private class TagAwareUnificator(
    val verse: TagComparisonVerse,
) : AbstractUnificator() {
    override fun checkTermsEquality(
        first: Term,
        second: Term,
    ): Boolean = first == second

    override fun handleEquation(
        request: Request,
        equation: Equation,
    ): Equation =
        with(equation) {
            when {
                isContradiction -> this
                else -> if (checkTagVerse(verse)) this else toContradiction()
            }
        }

    private fun Equation.checkTagVerse(verse: TagComparisonVerse): Boolean =
        when (verse) {
            TagComparisonVerse.EQUALS -> rhs.tags == lhs.tags
            TagComparisonVerse.IGNORE -> true
        }
}

/**
 * A [Unificator][it.unibo.tuprolog.unify.Unificator] that lets a developer debug where tags come from,
 * during resolution: it records every observed cross-contamination between the tags originated on the goal
 * side and the ones originated on the theory side, of any [Equation] handled while solving.
 */
private class TagFlowUnificator : AbstractUnificator() {
    val violations = mutableListOf<String>()
    var sawGoalOriginatedTag = false
    var sawTheoryOriginatedTag = false

    override fun checkTermsEquality(
        first: Term,
        second: Term,
    ): Boolean = first == second

    override fun handleEquation(
        request: Request,
        equation: Equation,
    ): Equation {
        val lhsOrigin = equation.lhs.tags["origin"]
        val rhsOrigin = equation.rhs.tags["origin"]
        if (lhsOrigin == "theory") violations += "theory-originated tag found in lhs: ${equation.lhs}"
        if (rhsOrigin == "goal") violations += "goal-originated tag found in rhs: ${equation.rhs}"
        if (lhsOrigin == "goal") sawGoalOriginatedTag = true
        if (rhsOrigin == "theory") sawTheoryOriginatedTag = true
        return equation
    }
}
