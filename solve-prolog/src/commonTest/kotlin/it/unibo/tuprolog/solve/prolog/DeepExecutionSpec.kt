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
        val budget = SolverTest.stepBudget(STEPS)

        test("deep/tail-recursion") {
            PrologSolverFactory.answers(
                "count($TAIL_DEPTH)",
                "count(0) :- !.",
                "count(N) :- M is N - 1, count(M).",
                budget = budget,
            ) shouldBe listOf("yes")
        }

        test("deep/non-tail-recursion") {
            PrologSolverFactory.answers(
                "findall(X, between(1, $DEPTH, X), _L), len(_L, N)",
                "len([], 0).",
                "len([_ | T], N) :- len(T, M), N is M + 1.",
                budget = budget,
            ) shouldBe listOf("yes: N = $DEPTH")
        }

        test("deep/redo-through-deep-stack") {
            // every frame of nest/1 leaves a choice point, and the final failure backtracks through all of them
            PrologSolverFactory.answers(
                "nest($DEPTH), fail",
                "nest(0) :- !.",
                "nest(N) :- M is N - 1, (nest(M) ; fail).",
                budget = budget,
            ) shouldBe listOf("no")
        }

        test("deep/long-list-unification-and-copy") {
            PrologSolverFactory.answers(
                "findall(f(_, Y), between(1, $DEPTH, Y), _L), copy_term(_L, _C), _C = _L, last(_C, f(_, X))",
                "last([X], X) :- !.",
                "last([_ | T], X) :- last(T, X).",
                budget = budget,
            ) shouldBe listOf("yes: X = $DEPTH")
        }
    }) {
    companion object {
        const val TAIL_DEPTH = 20_000

        // shortcut: the reference FSM is superlinear on these (at 2000: non-tail 186 s, redo 100 s, copy 241 s on JVM),
        // so they stay shallow; raise to TAIL_DEPTH once the #1010 engine is the default
        const val DEPTH = 300
        const val STEPS = 10_000_000L
    }
}
