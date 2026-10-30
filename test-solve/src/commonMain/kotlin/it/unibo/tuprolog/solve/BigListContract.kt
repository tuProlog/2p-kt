package it.unibo.tuprolog.solve

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.dsl.theory.logicProgramming

/** A stress test that recursively builds a list of [BigListOptions.SIZE] elements (via a hand-written `biglist/2` predicate) and checks the resulting list is correct, to catch performance regressions or stack-depth issues that only manifest on deep/large resolutions. */
fun bigListContract(
    solverFactory: SolverFactory,
    disabled: Set<String> = emptySet(),
) = contract(solverFactory, disabled) {
    // Tests the query
    // ```prolog
    // ?- biglist(N, L).
    // ```
    // against a theory recursively defining `biglist(N, [N|Rest])` down to `biglist(0, [0])`, with `N` bound to
    // [BigListOptions.SIZE]; succeeds producing 1 solution which binds `L` to the list `[N, N-1, ..., 1, 0]`,
    // followed by a `no` (no further solutions on backtracking).
    scenario("big-list/big-list-generation") {
        logicProgramming {
            val theory =
                theoryOf(
                    fact { "biglist"(0, logicListOf(0)) },
                    rule {
                        "biglist"(N, consOf(N, X)).impliedBy(
                            N greaterThan 0,
                            M `is` (N - 1),
                            "biglist"(M, X),
                        )
                    },
                )

            val solver = solverFactory.solverWithDefaultBuiltins(staticKb = theory)

            val query = "biglist"(BigListOptions.SIZE, L)
            // no timeout: this checks the result, and a wall-clock limit only fails it on a slow machine
            val solutions = solver.solve(query).toList()

            assertSolutionEquals(
                listOf(
                    query.yes(
                        L to logicListOf((0..BigListOptions.SIZE).reversed().map { Integer.of(it) }),
                    ),
                    query.no(),
                ),
                solutions,
            )
        }
    }
}
