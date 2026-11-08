package it.unibo.tuprolog.solve.prolog

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.solve.SolverTest
import it.unibo.tuprolog.solve.answers

/**
 * Deep executions that must not depend on the host stack (#1010, step 0). Besides the usual test runs, the JVM's
 * `jvmTestSmallStack` runs this spec with a 1 MB stack, so that accidental recursion in the engine shows up.
 */
class DeepExecutionSpec :
    FunSpec({
        val factory = PrologSolverFactory
        val budget = SolverTest.stepBudget(STEPS)
        val depth = DEPTH

        test("deep/tail-recursion") {
            factory.answers(
                "count($TAIL_DEPTH)",
                "count(0) :- !.",
                "count(N) :- M is N - 1, count(M).",
                budget = budget,
            ) shouldBe listOf("yes")
        }

        test("deep/non-tail-recursion") {
            factory.answers(
                "findall(X, between(1, $depth, X), _L), len(_L, N)",
                "len([], 0).",
                "len([_ | T], N) :- len(T, M), N is M + 1.",
                budget = budget,
            ) shouldBe listOf("yes: N = $depth")
        }

        test("deep/redo-through-deep-stack") {
            // every frame of nest/1 leaves a choice point, and the final failure backtracks through all of them
            factory.answers(
                "nest($depth), fail",
                "nest(0) :- !.",
                "nest(N) :- M is N - 1, (nest(M) ; fail).",
                budget = budget,
            ) shouldBe listOf("no")
        }

        test("deep/long-list-unification-and-copy") {
            factory.answers(
                "findall(f(_, Y), between(1, $depth, Y), _L), copy_term(_L, _C), _C = _L, last(_C, f(_, X))",
                "last([X], X) :- !.",
                "last([_ | T], X) :- last(T, X).",
                budget = budget,
            ) shouldBe listOf("yes: X = $depth")
        }
    }) {
    companion object {
        const val TAIL_DEPTH = 20_000

        const val DEPTH = TAIL_DEPTH
        const val STEPS = 10_000_000L
    }
}
