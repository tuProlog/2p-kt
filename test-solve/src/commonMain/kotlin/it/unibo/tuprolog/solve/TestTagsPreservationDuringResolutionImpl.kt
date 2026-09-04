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

class TestTagsPreservationDuringResolutionImpl(
    val solverFactory: SolverFactory,
) : TestTagsPreservationDuringResolution {
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

    private fun solverFor(
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

    override fun testUntaggedQueryAgainstTaggedTheory() {
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

    override fun testQueryTaggedLikeTheory() {
        for (depth in listOf(Depth.F, Depth.G, Depth.X)) {
            for (verse in verses) {
                val solutions = countSolutions(solverFor(depth, verse), taggedQuery(depth, tagA))
                assertEquals(1, solutions, "depth=$depth verse=$verse")
            }
        }
    }

    override fun testQueryTaggedDifferentlyFromTheory() {
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

    override fun testTagOriginDuringComputation() {
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
        assertTrue(unificator.sawGoalOriginatedTag, "goal-originated tags were never observed: they may have been lost")
        assertTrue(
            unificator.sawTheoryOriginatedTag,
            "theory-originated tags were never observed: they may have been lost",
        )
    }

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
}
