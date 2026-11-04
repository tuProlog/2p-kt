package it.unibo.tuprolog.examples.benchmarks.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.parsing.parse
import it.unibo.tuprolog.solve.Solver
import it.unibo.tuprolog.solve.prolog.PrologSolverFactory
import it.unibo.tuprolog.theory.MutableTheory
import it.unibo.tuprolog.theory.Theory
import it.unibo.tuprolog.theory.parsing.ClausesParser
import it.unibo.tuprolog.unify.Unificator
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State

/** A query over [TheorySolverWorkload.solver]'s knowledge bases, solved to exhaustion (#1019). */
enum class TheorySolverWorkload(
    val id: String,
    query: String,
) {
    /** 500 lookups bound on the first argument of a 10k-fact static table. */
    FACT_TABLE_ARG0("fact-table-arg0", "lookup_arg0(0, 500)"),

    /** 500 lookups bound on the second argument only of the same table. */
    FACT_TABLE_ARG1("fact-table-arg1", "lookup_arg1(9500, 10000)"),

    /** 500 lookups bound on the first argument of a 10k-fact dynamic table. */
    DYNAMIC_TABLE_ARG0("dynamic-table-arg0", "lookup_dynamic(0, 500)"),

    /** A dynamic counter updated 500 times via retract/assertz. */
    DYNAMIC_COUNTER("dynamic-counter", "counter(500)"),

    /** 500 assertz/retract pairs of fresh facts. */
    DYNAMIC_CHURN("dynamic-churn", "churn(0, 500)"),

    /** A 1000-step walk along a chain, deterministic by first-argument indexing. */
    DETERMINISM("determinism", "walk(0)"),
    ;

    val goal: Struct = Struct.parse(query)

    companion object {
        const val TABLE_SIZE = 10_000
        const val CHAIN_LENGTH = 1000

        fun byId(id: String): TheorySolverWorkload = entries.first { it.id == id }

        private fun table(name: String): List<Clause> =
            List(TABLE_SIZE) { Fact.of(Struct.of(name, Integer.of(it), Integer.of(TABLE_SIZE - it))) }

        val staticKb: Theory by lazy {
            val text =
                TheorySolverWorkload::class.java
                    .getResource("/it/unibo/tuprolog/examples/benchmarks/theory-workloads.pl")!!
                    .readText()
            val chain = List(CHAIN_LENGTH) { Fact.of(Struct.of("step", Integer.of(it), Integer.of(it + 1))) }
            ClausesParser.withDefaultOperators().parseTheory(text).assertZ(table("t") + chain)
        }

        /** A fresh solver: its dynamic KB holds `dt/2`, and the workloads that update it restore it. */
        fun solver(): Solver =
            PrologSolverFactory.solverWithDefaultBuiltins(
                staticKb = staticKb,
                dynamicKb = MutableTheory.of(Unificator.default, table("dt")),
            )
    }
}

/** Average time to solve each [TheorySolverWorkload] to exhaustion with `:solve-prolog` (#1019). */
@State(Scope.Benchmark)
class TheorySolverBenchmark {
    @Param(
        "fact-table-arg0",
        "fact-table-arg1",
        "dynamic-table-arg0",
        "dynamic-counter",
        "dynamic-churn",
        "determinism",
    )
    var workload: String = "fact-table-arg0"

    private lateinit var solver: Solver
    private lateinit var goal: Struct

    @Setup
    fun setUp() {
        solver = TheorySolverWorkload.solver()
        goal = TheorySolverWorkload.byId(workload).goal
    }

    @Benchmark
    fun solveAll(blackhole: Blackhole) {
        for (solution in solver.solve(goal)) {
            blackhole.consume(solution)
        }
    }
}
