package it.unibo.tuprolog.solve

import io.kotest.assertions.withClue
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.solve.tags.TagComparisonVerse
import it.unibo.tuprolog.solve.tags.TagFlow
import it.unibo.tuprolog.solve.tags.TagsUnificationEngine
import it.unibo.tuprolog.utils.setTags

/** Verifies that term tags (see `it.unibo.tuprolog.utils.Taggable`) attached to a theory's clauses, or to a query, survive resolution and are visible to a custom `it.unibo.tuprolog.unify.Unificator`. */
fun tagsPreservationDuringResolutionContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    // Every scenario runs against both unification engines (see TagsUnificationEngine), which differ in which tags
    // are observable: the worklist engine compares compound terms before decomposing them, so tags at every depth are
    // visible (at depth `CLAUSE` too, as clauses are retrieved from the theory by unifying them as a whole), whereas
    // the equation-based one decomposes matching compound terms straight into equations of their arguments, so only
    // tags at depth `X` are.

    // Case 1: the theory clause is tagged (at each of the four depths), and the query is *not*.
    //
    // Under `IGNORE`, resolution always succeeds (tags play no role). Under `EQUALS`, it only fails when the tag
    // is actually observable, since an empty query-side tag map differs from the clause's.
    scenario("tags-preservation-during-resolution/untagged-query-against-tagged-theory") {
        for (engine in TagsUnificationEngine.entries) {
            for (depth in Depth.entries) {
                for (verse in verses) {
                    val solutions = countSolutions(solverFor(engine, depth, verse), untaggedQuery())
                    val expected = if (verse == TagComparisonVerse.IGNORE || !engine.sees(depth)) 1 else 0
                    withClue("engine=$engine depth=$depth verse=$verse") { solutions shouldBe expected }
                }
            }
        }
    }

    // Case 2: the query is tagged exactly like the theory clause, at the same depth (`F`, `G` or `X`; `CLAUSE` has
    // no query counterpart, since a query is a plain goal, not a wrapping clause).
    //
    // Resolution always succeeds, with exactly one solution, under both `EQUALS` and `IGNORE`.
    scenario("tags-preservation-during-resolution/query-tagged-like-theory") {
        for (engine in TagsUnificationEngine.entries) {
            for (depth in listOf(Depth.F, Depth.G, Depth.X)) {
                for (verse in verses) {
                    val solutions = countSolutions(solverFor(engine, depth, verse), taggedQuery(depth, tagA))
                    withClue("engine=$engine depth=$depth verse=$verse") { solutions shouldBe 1 }
                }
            }
        }
    }

    // Case 3: the query is tagged differently from the theory clause, at the same depth (`F`, `G` or `X`) — once
    // with a different tag value for the same key, once with a different key altogether.
    //
    // Under `IGNORE`, resolution still succeeds. Under `EQUALS`, it only fails where the mismatch is actually
    // observable; elsewhere, resolution succeeds regardless, exactly as it would with a fully untagged query.
    scenario("tags-preservation-during-resolution/query-tagged-differently-from-theory") {
        for (engine in TagsUnificationEngine.entries) {
            for (depth in listOf(Depth.F, Depth.G, Depth.X)) {
                for (differentTag in listOf(tagDifferentValue, tagDifferentKey)) {
                    for (verse in verses) {
                        val solver = solverFor(engine, depth, verse)
                        val solutions = countSolutions(solver, taggedQuery(depth, differentTag))
                        val expected = if (verse == TagComparisonVerse.IGNORE || !engine.sees(depth)) 1 else 0
                        withClue("engine=$engine depth=$depth tag=$differentTag verse=$verse") {
                            solutions shouldBe expected
                        }
                    }
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
    // the theory (`zero`, and each `succ` wrapper) is tagged as theory-originated. A custom unificator inspects every
    // comparison (or equation) handled during resolution and fails the test if a goal-originated tag is ever found on
    // its right-hand side, or a theory-originated tag is ever found on its left-hand side — since resolution always unifies the current goal
    // (lhs) against a clause head (rhs), a correct implementation should never mix the two. It also fails if
    // neither kind of tag was ever observed at all, which would indicate tags silently disappeared during
    // resolution rather than legitimately never crossing sides.
    //
    // This test is meant to help a developer pinpoint *where*, during resolution, a tag gets lost or misplaced.
    scenario("tags-preservation-during-resolution/tag-origin-during-computation") {
        val goalTag = mapOf(TagFlow.ORIGIN to TagFlow.GOAL)
        val theoryTag = mapOf(TagFlow.ORIGIN to TagFlow.THEORY)

        val theory =
            logicProgramming {
                theoryOf(
                    fact { "count"("e", atomOf("zero").setTags(theoryTag)) },
                    rule {
                        "count"("l"(A, B), "succ"(C).setTags(theoryTag)) impliedBy "count"(B, C)
                    },
                )
            }
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

        for (engine in TagsUnificationEngine.entries) {
            val flow = TagFlow()
            val solver = solverFactory.solverWithDefaultBuiltins(staticKb = theory, unificator = engine.tagFlow(flow))
            val solutions = solver.solveList(goal).filter { it.isYes }

            withClue("engine=$engine") {
                solutions.size shouldBe 1
                withClue("tags crossed sides: ${flow.violations}") { flow.violations.isEmpty().shouldBeTrue() }
                withClue("goal-originated tags were never observed: they may have been lost") {
                    flow.sawGoalOriginatedTag.shouldBeTrue()
                }
                withClue("theory-originated tags were never observed: they may have been lost") {
                    flow.sawTheoryOriginatedTag.shouldBeTrue()
                }
            }
        }
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

/** Whether this engine lets custom unificators see tags attached at [depth]. */
private fun TagsUnificationEngine.sees(depth: Depth): Boolean =
    when (depth) {
        Depth.CLAUSE, Depth.F, Depth.G -> compoundTagsVisible
        Depth.X -> true
    }

private fun ContractScope.solverFor(
    engine: TagsUnificationEngine,
    depth: Depth,
    verse: TagComparisonVerse,
): Solver {
    val theory = logicProgramming { theoryOf(taggedClause(depth, tagA)) }
    return solverFactory.solverWithDefaultBuiltins(staticKb = theory, unificator = engine.tagAware(verse))
}

private fun countSolutions(
    solver: Solver,
    goal: Struct,
): Int = solver.solveList(goal).count { it.isYes }
