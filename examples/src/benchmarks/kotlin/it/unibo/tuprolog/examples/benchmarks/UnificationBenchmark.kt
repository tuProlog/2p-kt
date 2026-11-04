package it.unibo.tuprolog.examples.benchmarks

import it.unibo.tuprolog.unify.Unificator
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State

/** Average time of one [UnificationScenario], per unification engine and occurs-check setting (#1020). */
@State(Scope.Benchmark)
class UnificationBenchmark {
    @Param(
        "ground-equal",
        "early-mismatch",
        "late-mismatch",
        "many-variables",
        "variable-chain",
        "repeated-variable",
        "repeated-variable-failing",
        "deep-terms",
        "list-equal",
        "list-with-variables",
        "list-early-mismatch",
        "list-late-mismatch",
        "partial-list",
        "occurs-near-start",
        "occurs-near-end",
        "occurs-nowhere",
        "self-reference",
        "merge-disjoint",
        "merge-compatible",
        "merge-incompatible",
        "merge-chain",
    )
    var scenario: String = "ground-equal"

    @Param("equational", "worklist")
    var engine: String = "worklist"

    @Param("true", "false")
    var occursCheck: Boolean = true

    private lateinit var unificator: Unificator
    private lateinit var task: UnificationTask

    @Setup
    fun setUp() {
        unificator = UnificatorSetting.byId(engine).unificator
        task = UnificationScenario.byId(scenario).task()
    }

    @Benchmark
    fun unify(): Any = task.runWith(unificator, occursCheck)
}
