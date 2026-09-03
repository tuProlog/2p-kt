package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.dsl.theory.logicProgramming
import it.unibo.tuprolog.unify.AbstractUnificator
import it.unibo.tuprolog.unify.Equation
import it.unibo.tuprolog.utils.setTags
import kotlin.test.assertEquals

class TestTagsPreservationDuringResolutionImpl(
    val solverFactory: SolverFactory,
) : TestTagsPreservationDuringResolution {
    private fun <T : Term> T.setTags(n: Int): T {
        require(n >= 0)
        if (n == 0) return this
        val tags = (1..n).associate { "k$it" to "v$it" }
        return this.setTags(tags)
    }

    val theory =
        logicProgramming {
            theoryOf(
                sequence {
                    for (i in 0..2) {
                        yield(fact { "f"("g"("x")).setTags(i) })
                        yield(fact { "f"("g"("x").setTags(i)) })
                        yield(fact { "f"("g"(atom("x").setTags(i))) })
                    }
                },
            )
        }

    override fun testLeft() {
        val unificator = TagAwareUnificator(TagComparisonVerse.SUPERSET)
        val solver = solverFactory.solverWithDefaultBuiltins(staticKb = theory, unificator = unificator)
        val goal1 =
            logicProgramming {
                "f"("g"(X)).setTags(1)
            }
        val solutions1 = solver.solveList(goal1, shortDuration).filter { it.isYes }
        assertEquals(2, solutions1.size)
        val goal2 =
            logicProgramming {
                "f"("g"(X).setTags(1))
            }
        val solutions2 = solver.solveList(goal2, shortDuration).filter { it.isYes }
        assertEquals(2, solutions2.size)
        val goal3 =
            logicProgramming {
                "f"("g"(X.setTags(1)))
            }
        val solutions3 = solver.solveList(goal3, shortDuration).filter { it.isYes }
        assertEquals(2, solutions3.size)
    }

    override fun testRight() {
        val unificator = TagAwareUnificator(TagComparisonVerse.SUBSET)
        val solver = solverFactory.solverWithDefaultBuiltins(staticKb = theory, unificator = unificator)
        val goal1 =
            logicProgramming {
                "f"("g"(X)).setTags(0)
            }
        val solutions1 = solver.solveList(goal1, shortDuration).filter { it.isYes }
        assertEquals(9, solutions1.size)
        val goal2 =
            logicProgramming {
                "f"("g"(X).setTags(0))
            }
        val solutions2 = solver.solveList(goal2, shortDuration).filter { it.isYes }
        assertEquals(9, solutions2.size)
        val goal3 =
            logicProgramming {
                "f"("g"(X.setTags(0)))
            }
        val solutions3 = solver.solveList(goal3, shortDuration).filter { it.isYes }
        assertEquals(9, solutions3.size)
    }

    override fun testSymmetric() {
        val unificator = TagAwareUnificator(TagComparisonVerse.EQUALS)
        val solver = solverFactory.solverWithDefaultBuiltins(staticKb = theory, unificator = unificator)
        val goal1 =
            logicProgramming {
                "f"("g"(X)).setTags(2)
            }
        val solutions1 = solver.solveList(goal1, shortDuration).filter { it.isYes }
        assertEquals(1, solutions1.size)
        val goal2 =
            logicProgramming {
                "f"("g"(X).setTags(2))
            }
        val solutions2 = solver.solveList(goal2, shortDuration).filter { it.isYes }
        assertEquals(1, solutions2.size)
        val goal3 =
            logicProgramming {
                "f"("g"(X.setTags(2)))
            }
        val solutions3 = solver.solveList(goal3, shortDuration).filter { it.isYes }
        assertEquals(1, solutions3.size)
    }

    override fun testNone() {
        val unificator = TagAwareUnificator(TagComparisonVerse.IGNORE)
        val solver = solverFactory.solverWithDefaultBuiltins(staticKb = theory, unificator = unificator)
        val goal1 =
            logicProgramming {
                "f"("g"(X)).setTags(3)
            }
        val solutions1 = solver.solveList(goal1, shortDuration).filter { it.isYes }
        assertEquals(9, solutions1.size)
        val goal2 =
            logicProgramming {
                "f"("g"(X).setTags(4))
            }
        val solutions2 = solver.solveList(goal2, shortDuration).filter { it.isYes }
        assertEquals(9, solutions2.size)
        val goal3 =
            logicProgramming {
                "f"("g"(X.setTags(5)))
            }
        val solutions3 = solver.solveList(goal3, shortDuration).filter { it.isYes }
        assertEquals(9, solutions3.size)
    }

    enum class TagComparisonVerse {
        SUPERSET,
        SUBSET,
        EQUALS,
        IGNORE,
    }

    class TagAwareUnificator(
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
                TagComparisonVerse.SUPERSET -> lhs.tags.superSet(rhs.tags)
                TagComparisonVerse.SUBSET -> rhs.tags.superSet(lhs.tags)
                TagComparisonVerse.EQUALS -> rhs.tags == lhs.tags
                TagComparisonVerse.IGNORE -> true
            }

        private fun <K, V> Map<K, V>.superSet(other: Map<K, V>): Boolean =
            this.size >= other.size && this.keys.containsAll(other.keys) && other.all { this[it.key] == it.value }
    }
}
