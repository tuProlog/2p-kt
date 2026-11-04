package it.unibo.tuprolog.examples.benchmarks.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.theory.Theory
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State

/**
 * Whole-theory operations (#1019) on `size` facts spread over `predicates` procedures ([TheoryWorkloads.spread]).
 * `iterate-after-update` asserts and retracts a clause first: on mutable theories this invalidates whatever the
 * implementation caches for iteration; on immutable ones it iterates a new theory.
 */
@State(Scope.Benchmark)
class TheoryIterationBenchmark {
    @Param("immutable", "mutable", "listed", "mutable-listed")
    var impl: String = "immutable"

    @Param("1000", "100000")
    var size: Int = 1000

    @Param("1", "1000")
    var predicates: Int = 1

    @Param("iterate", "iterate-after-update", "size", "hashCode", "equals")
    var op: String = "iterate"

    private lateinit var theory: Theory
    private lateinit var twin: Theory
    private lateinit var extra: Clause

    @Setup
    fun setUp() {
        val implementation = TheoryImpl.byId(impl)
        val clauses = TheoryWorkloads.spread(size, predicates)
        theory = implementation.build(clauses)
        twin = implementation.build(clauses)
        extra = Fact.of(Struct.of("k_0", TheoryWorkloads.c(-1)))
    }

    @Benchmark
    fun whole(): Any =
        when (op) {
            "iterate" -> theory.clauses.count()
            "iterate-after-update" -> {
                val updated = theory.assertZ(extra)
                val count = updated.clauses.count()
                if (updated === theory) theory.retract(extra)
                count
            }
            "size" -> theory.size
            "hashCode" -> theory.hashCode()
            "equals" -> theory == twin
            else -> error("Unknown op: $op")
        }
}
