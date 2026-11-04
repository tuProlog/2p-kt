package it.unibo.tuprolog.examples.benchmarks.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.Theory
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State

/**
 * Average time of one [Theory.get] (#1019) against [TheoryWorkloads.standard], per implementation, size and query:
 * `first` consumes the first match only (which is also what `contains` costs), `all` counts every match.
 */
@State(Scope.Benchmark)
class TheoryLookupBenchmark {
    @Param("immutable", "mutable", "listed", "mutable-listed")
    var impl: String = "immutable"

    @Param("10", "1000", "100000")
    var size: Int = 10

    @Param("arg0-bound", "arg1-bound", "last-bound", "all-vars", "zero-arity", "nested-arg0", "no-match")
    var query: String = "arg0-bound"

    private lateinit var theory: Theory
    private lateinit var pattern: Clause

    @Setup
    fun setUp() {
        theory = TheoryImpl.byId(impl).build(TheoryWorkloads.standard(size))
        pattern = TheoryQuery.byId(query).query(size)
    }

    @Benchmark
    fun first(): Any? = theory[pattern].firstOrNull()

    @Benchmark
    fun all(): Int = theory[pattern].count()
}
