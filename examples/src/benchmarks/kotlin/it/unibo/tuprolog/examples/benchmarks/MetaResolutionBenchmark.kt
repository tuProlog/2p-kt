package it.unibo.tuprolog.examples.benchmarks

import it.unibo.tuprolog.core.Compound
import it.unibo.tuprolog.core.parsing.parseAsCompound
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.parsing.parse
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.Param
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.Setup
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.infra.Blackhole

/**
 * Average time to solve to exhaustion queries whose functors are variables, and calls going through metarules, with
 * `:solve-prolog`, on [size] facts spread over 20 unary predicates (`p_0(0)`, `p_1(1)`, ...), [size] facts of the
 * ternary `stored/3`, and a chain of [size] `parent/2` facts:
 * - `baseline`: `p_7(X)`, a concrete query on a theory without metarules;
 * - `enumerate`: `F(X)`, all the unary facts;
 * - `filter`: `F(N)`, for the middle fact, through the argument indexes of the clauses of arity 1;
 * - `virtual`: `rel_7(X, Y)`, through the metarule `P(X, Y) :- stored(P, X, Y)`;
 * - `compose`: `grandparent(X, Y)`, through `P(X, Y) :- compose(P, Q, R), Q(X, Z), R(Z, Y)`.
 */
@State(Scope.Benchmark)
class MetaResolutionBenchmark {
    @Param("baseline", "enumerate", "filter", "virtual", "compose")
    var workload: String = "enumerate"

    @Param("100", "1000")
    var size: Int = 100

    private lateinit var solver: Solver
    private lateinit var goal: Compound

    @Setup
    fun setUp() {
        val facts = (0 until size).joinToString("\n") { "p_${it % PREDICATES}($it)." }
        val clauses =
            when (workload) {
                "baseline", "enumerate", "filter" -> facts
                "virtual" ->
                    (0 until size).joinToString("\n") { "stored(rel_${it % PREDICATES}, $it, ${it + 1})." } +
                        "\nP(X, Y) :- stored(P, X, Y)."
                "compose" ->
                    (0 until size).joinToString("\n") { "parent($it, ${it + 1})." } +
                        "\ncompose(grandparent, parent, parent).\nP(X, Y) :- compose(P, Q, R), Q(X, Z), R(Z, Y)."
                else -> error("Unknown workload: $workload")
            }
        solver = Solver.prolog.solverWithDefaultBuiltins(staticKb = Theory.parse(clauses))
        goal =
            when (workload) {
                "baseline" -> "p_7(X)"
                "enumerate" -> "F(X)"
                "filter" -> "F(${size / 2})"
                "virtual" -> "rel_7(X, Y)"
                else -> "grandparent(X, Y)"
            }.parseAsCompound()
    }

    @Benchmark
    fun solveAll(blackhole: Blackhole) {
        for (solution in solver.solve(goal)) {
            blackhole.consume(solution)
        }
    }

    private companion object {
        const val PREDICATES = 20
    }
}
