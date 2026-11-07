package it.unibo.tuprolog.examples.benchmarks

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State

/**
 * Cost of creating variables: each one gets a fresh identifier, which makes up its complete name, which in turn
 * backs its equality and hash code. Variables either share one name, or cycle through many distinct ones.
 *
 * `./gradlew :examples:timingBenchmarks -PjmhArgs='VariableCreationBenchmark -wi 3 -i 5 -f 1'`; add `-t 4` to see
 * contention among threads creating variables concurrently.
 */
@State(Scope.Thread)
class VariableCreationBenchmark {
    @Param("same", "distinct")
    var names: String = "same"

    private lateinit var pool: Array<String>
    private var next = 0
    private lateinit var clause: Clause

    @Setup
    fun setUp() {
        pool = if (names == "same") arrayOf("X") else Array(DISTINCT_NAMES) { "X$it" }
        val vars = Array(CLAUSE_VARS) { Var.of("X$it") }
        // append(X0, X1, X2) :- append(X3, X4, X5), ... : a non-ground rule, as the solver renames at each call
        clause =
            Clause.of(
                Struct.of("append", vars[0], vars[1], vars[2]),
                *Array(CLAUSE_VARS / 2) { Struct.of("p", vars[it], vars[it + CLAUSE_VARS / 2]) },
            )
    }

    private fun name(): String = pool[next++ % pool.size]

    @Benchmark
    fun create(): Var = Var.of(name())

    @Benchmark
    fun createAndHash(): Int = Var.of(name()).hashCode()

    @Benchmark
    fun freshCopyClause(): Clause = clause.freshCopy()

    private companion object {
        const val DISTINCT_NAMES = 10_000
        const val CLAUSE_VARS = 6
    }
}
