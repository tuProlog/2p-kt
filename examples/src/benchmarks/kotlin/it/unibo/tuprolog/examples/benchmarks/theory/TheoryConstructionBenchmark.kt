package it.unibo.tuprolog.examples.benchmarks.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.Theory
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State

/** Average time to build a theory of [TheoryWorkloads.standard] clauses (3 × size + 1) in one go (#1019). */
@State(Scope.Benchmark)
class TheoryConstructionBenchmark {
    @Param("immutable", "mutable", "listed", "mutable-listed")
    var impl: String = "immutable"

    @Param("10", "100", "1000", "10000", "100000")
    var size: Int = 10

    private lateinit var implementation: TheoryImpl
    private lateinit var clauses: List<Clause>

    @Setup
    fun setUp() {
        implementation = TheoryImpl.byId(impl)
        clauses = TheoryWorkloads.standard(size)
    }

    @Benchmark
    fun bulk(): Theory = implementation.build(clauses)
}

/**
 * Average time to build the same theory one `assertZ` at a time (#1019). Sizes stop at 1000: on immutable theories
 * this is quadratic before #1019.
 */
@State(Scope.Benchmark)
class TheoryIncrementalConstructionBenchmark {
    @Param("immutable", "mutable", "listed", "mutable-listed")
    var impl: String = "immutable"

    @Param("10", "100", "1000")
    var size: Int = 10

    private lateinit var implementation: TheoryImpl
    private lateinit var clauses: List<Clause>

    @Setup
    fun setUp() {
        implementation = TheoryImpl.byId(impl)
        clauses = TheoryWorkloads.standard(size)
    }

    @Benchmark
    fun incremental(): Theory {
        var theory = implementation.build(emptyList())
        for (clause in clauses) theory = theory.assertZ(clause)
        return theory
    }
}
